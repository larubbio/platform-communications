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
import org.motechproject.sms.templates.Template;
import org.motechproject.sms.templates.TemplateReader;
import org.motechproject.sms.templates.Templates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.sms.audit.DeliveryStatus.PENDING;
import static org.motechproject.sms.audit.DeliveryStatus.SCHEDULED;
import static org.motechproject.sms.audit.SmsType.OUTBOUND;
import static org.motechproject.sms.event.SmsEvents.makeScheduledSendEvent;
import static org.motechproject.sms.event.SmsEvents.makeSendEvent;

//todo: final pass over how we use motechId system-wide

/**
 * todo
 */
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

    private ArrayList<ArrayList<String>> chunkList(List<String> list, Integer chunkSize) {
        ArrayList<ArrayList<String>> ret = new ArrayList<ArrayList<String>>();
        Integer i = 0;
        ArrayList<String> chunk = new ArrayList<String>();
        for (String val : list) {
            chunk.add(val);
            i++;
            if (i % chunkSize == 0) {
                ret.add(chunk);
                chunk = new ArrayList<String>();
            }
        }
        if (chunk.size() > 0) {
            ret.add(chunk);
        }
        return ret;
    }

    private String generateMotechId() {
        return UUID.randomUUID().toString().replace("-", "");
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

        if (sms.hasConfig()) {
            config = configs.getConfig(sms.getConfig());
        }
        else {
            logger.debug("No config specified, using default config.");
            config = configs.getDefaultConfig();
        }
        template = templates.getTemplate(config.getTemplateName());

        //todo: die if things aren't right, right?
        //todo: SMS_SCHEDULE_FUTURE_SMS research if any sms provider provides that, for now assume not.

        Integer maxSize = template.getOutgoing().getMaxSmsSize();
        String header = config.getSplitHeader();
        String footer = config.getSplitFooter();
        Boolean excludeLastFooter = config.getExcludeLastFooter();
        //todo: maximum number of supported recipients : per template/provider and/or per http specs

        // -2 to account for the added \n after the header and before the footer
        if ((maxSize - header.length() - footer.length() - 2) <= 0) {
            throw new IllegalArgumentException(
                    "The combined sizes of the header and footer templates are larger than the maximum SMS size!");
        }

        List<String> messageParts = splitMessage(sms.getMessage(), maxSize, header, footer, excludeLastFooter);
        ArrayList<ArrayList<String>> recipientsChunks = chunkList(sms.getRecipients(),
                template.getOutgoing().getMaxRecipient());

        //todo: delivery_time on the sms provider's side if they support it?
        for (List<String> chunk : recipientsChunks) {
            if (sms.hasDeliveryTime()) {
                DateTime dt = sms.getDeliveryTime();
                for (String part : messageParts) {
                    String motechId = generateMotechId();
                    schedulerService.safeScheduleRunOnceJob(new RunOnceSchedulableJob(makeScheduledSendEvent(
                        config.getName(), chunk, part, motechId, null), dt.toDate()));
                    logger.info(String.format("Scheduling message [%s] to [%s] at %s.",
                            part.replace("\n", "\\n"), chunk, sms.getDeliveryTime()));
                    //add one millisecond to the next sms part so they will be delivered in order
                    //without that it seems Quartz doesn't fire events in the order they were scheduled
                    dt = dt.plus(1);
                    for (String recipient : chunk) {
                        smsAuditService.log(new SmsRecord(config.getName(), OUTBOUND, recipient, part, now(), SCHEDULED,
                                null, motechId, null, null));
                    }
                }
            }
            else {
                for (String part : messageParts) {
                    String motechId = generateMotechId();
                    eventRelay.sendEventMessage(makeSendEvent(config.getName(), chunk, part, motechId, null));
                    logger.info("Sending message [{}] to [{}].", part.replace("\n", "\\n"), chunk);
                    for (String recipient : chunk) {
                        smsAuditService.log(new SmsRecord(config.getName(), OUTBOUND, recipient, part, now(), PENDING,
                                null, motechId, null, null));
                    }
                }
            }
        }
    }
}
