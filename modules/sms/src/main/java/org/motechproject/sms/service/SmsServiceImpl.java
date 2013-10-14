package org.motechproject.sms.service;

import org.joda.time.DateTime;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.audit.SmsRecord;
import org.motechproject.sms.configs.Config;
import org.motechproject.sms.configs.ConfigReader;
import org.motechproject.sms.configs.Configs;
import org.motechproject.sms.event.SmsEvents;
import org.motechproject.sms.templates.Template;
import org.motechproject.sms.templates.TemplateReader;
import org.motechproject.sms.templates.Templates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.sms.audit.SmsDeliveryStatus.PENDING;
import static org.motechproject.sms.audit.SmsDeliveryStatus.SCHEDULED;
import static org.motechproject.sms.audit.SmsType.OUTBOUND;

@Service("smsService")
public class SmsServiceImpl implements SmsService {

    private SettingsFacade settingsFacade;
    private Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);
    private EventRelay eventRelay;
    private MotechSchedulerService schedulerService;
    private Templates templates;
    private SmsAuditService smsAuditService;

    @Autowired
    public SmsServiceImpl(@Qualifier("smsSettings") SettingsFacade settingsFacade, EventRelay eventRelay,
                          MotechSchedulerService schedulerService, TemplateReader templateReader,
                          SmsAuditService smsAuditService) {
        //todo: persist configs or reload them for each call?
        //todo: right now I'm doing the latter...
        //todo: ... but I'm not wed to it.
        this.settingsFacade = settingsFacade;
        this.eventRelay = eventRelay;
        this.schedulerService = schedulerService;
        templates = templateReader.getTemplates();
        this.smsAuditService = smsAuditService;
    }

     static private List<String> splitMessage(String message, int maxSize, String headerTemplate, String footerTemplate,
                                              boolean excludeLastFooter) {
        List<String> parts = new ArrayList<String>();
        int messageLength = message.length();

        if (messageLength <= maxSize) {
            parts.add(message);
        } else {
            //NOTE: since the format placeholders $m and $t are two characters wide and we assume no more than
            //99 parts, we don't need to do a String.format() to figure out the length of the actual header/footer
            headerTemplate = headerTemplate + "\n";
            footerTemplate = "\n" + footerTemplate;
            Integer textSize = maxSize - headerTemplate.length() - footerTemplate.length();
            Integer numberOfParts = (int) Math.ceil(messageLength / (double) textSize);
            String numberOfPartsString = numberOfParts.toString();

            for (Integer i=1; i<=numberOfParts; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(headerTemplate.replace("$m", i.toString()).replace("$t", numberOfPartsString));
                if (i == numberOfParts) {
                    sb.append(message.substring((i-1)*textSize));
                    if (!excludeLastFooter) {
                        sb.append(footerTemplate.replace("$m", i.toString()).replace("$t", numberOfPartsString));
                    }
                }
                else {
                    sb.append(message.substring((i-1)*textSize, (i-1)*textSize+textSize));
                    sb.append(footerTemplate.replace("$m", i.toString()).replace("$t", numberOfPartsString));
                }
                parts.add(sb.toString());
            }
        }
        return parts;
    }

    @Override
    /**
     * TODO
     */
    public void send(OutgoingSms sms){

        //todo: cache that!
        Configs configs = new ConfigReader(settingsFacade).getConfigs();
        Config config;
        Template template;
        Integer milliDelay;

        if (sms.hasConfig()) {
            config = configs.getConfig(sms.getConfig());
        }
        else {
            logger.info("No config specified, using default config.");
            config = configs.getDefaultConfig();
        }
        template = templates.getTemplate(config.getTemplateName());
        milliDelay = template.getOutgoing().getMillisecondsBetweenMessageChunks();

        if (!sms.hasMotechId()) {
            sms.setMotechId(UUID.randomUUID().toString().replace("-", ""));
        }

        //todo: die if things aren't right, right?
        //todo: SMS_SCHEDULE_FUTURE_SMS research if any sms provider provides that, for now assume not.

        Integer maxSize = template.getOutgoing().getMaxSmsSize();
        String header = config.getSplitHeader();
        String footer = config.getSplitFooter();
        Boolean excludeLastFooter = config.getExcludeLastFooter();
        //todo: maximum number of supported recipients : per template/provider and/or per http specs
        Boolean isMultiRecipientSupported = config.getMultiRecipientSupport();

        // -2 to account for the added \n after the header and before the footer
        if ((maxSize - header.length() - footer.length() - 2) <= 0) {
            throw new IllegalArgumentException(
                    "The combined sizes of the header and footer templates are larger than the maximum SMS size!");
        }

        List<String> messageParts = splitMessage(sms.getMessage(), maxSize, header, footer, excludeLastFooter);

        //todo: delivery_time on the sms provider's side if they support it?
        if (isMultiRecipientSupported) {
            if (sms.hasDeliveryTime()) {
                DateTime dt = sms.getDeliveryTime();
                for (String part : messageParts) {
                    RunOnceSchedulableJob job = new RunOnceSchedulableJob(SmsEvents.makeScheduledSendEvent(
                        config.getName(), sms.getRecipients(), part, sms.getMotechId(), null), dt.toDate());
                    schedulerService.safeScheduleRunOnceJob(job);
                    logger.info(String.format("Scheduling message [%s] to recipients %s at %s.",
                        part.replace("\n", "\\n"), sms.getRecipients(), sms.getDeliveryTime()));
                    //add (at least) one millisecond to the next sms part so they will be delivered in order
                    //without that it seems Quartz doesn't fire events in the order they were scheduled
                    dt = dt.plus(milliDelay);
                    for (String recipient : sms.getRecipients()) {
                        smsAuditService.log(new SmsRecord(config.getName(), OUTBOUND, recipient, part, now(), SCHEDULED,
                            sms.getMotechId(), null));
                    }
                }
            }
            else {
                for (String part : messageParts) {
                    eventRelay.sendEventMessage(SmsEvents.makeSendEvent(config.getName(), sms.getRecipients(), part,
                        sms.getMotechId(), null));
                    logger.info("Sending message [{}] to recipients {}.", part.replace("\n", "\\n"),
                        sms.getRecipients());
                    for (String recipient : sms.getRecipients()) {
                        smsAuditService.log(new SmsRecord(config.getName(), OUTBOUND, recipient, part, now(), PENDING,
                            sms.getMotechId(), null));
                    }
                }
            }
        } else {
            for (String recipient : sms.getRecipients()) {
                DateTime dt = sms.getDeliveryTime();
                for (String part : messageParts) {
                    if (sms.hasDeliveryTime()) {
                        if (template.getOutgoing().getMillisecondsBetweenMessageChunks() > 0) {
                            milliDelay = template.getOutgoing().getMillisecondsBetweenMessageChunks();
                        }
                        RunOnceSchedulableJob job = new RunOnceSchedulableJob(SmsEvents.makeScheduledSendEvent(
                            config.getName(), Arrays.asList(recipient), part, sms.getMotechId(), null), dt.toDate());
                        schedulerService.safeScheduleRunOnceJob(job);
                        logger.info(String.format("Scheduling message [%s] to recipient %s at %s.",
                            part.replace("\n", "\\n"), recipient, sms.getDeliveryTime()));
                        //add (at least) one millisecond to the next sms part so they will be delivered in order
                        //without that it seems Quartz doesn't fire events in the order they were scheduled
                        dt = dt.plus(milliDelay);
                        smsAuditService.log(new SmsRecord(config.getName(), OUTBOUND, recipient, part, now(), SCHEDULED,
                                sms.getMotechId(), null));
                    }
                    else {
                        logger.info("Sending message [{}] to recipient {}.", part.replace("\n", "\\n"), recipient);
                        eventRelay.sendEventMessage(SmsEvents.makeSendEvent(config.getName(), Arrays.asList(recipient),
                                part, sms.getMotechId(), null));
                        smsAuditService.log(new SmsRecord(config.getName(), OUTBOUND, recipient, part, now(), PENDING,
                                sms.getMotechId(), null));
                    }
                }
            }
        }
    }
}