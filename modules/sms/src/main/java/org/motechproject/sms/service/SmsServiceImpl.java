package org.motechproject.sms.service;

import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.event.SmsEvents;
import org.motechproject.sms.settings.Config;
import org.motechproject.sms.settings.ConfigsDto;
import org.motechproject.sms.settings.OutgoingSms;
import org.motechproject.sms.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("smsService")
public class SmsServiceImpl implements SmsService {

    private SettingsFacade settingsFacade;
    private Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);
    private EventRelay eventRelay;
    private MotechSchedulerService schedulerService;


    @Autowired
    public SmsServiceImpl(@Qualifier("smsSettings") SettingsFacade settingsFacade, EventRelay eventRelay, MotechSchedulerService schedulerService) {
        //todo: persist settings or reload them for each call?
        //todo: right now I'm doing the latter...
        //todo: ... but I'm not wed to it.
        this.settingsFacade = settingsFacade;
        this.eventRelay = eventRelay;
        this.schedulerService = schedulerService;
    }

     static private List<String> splitMessage(String message, int maxSize, String headerTemplate, String footerTemplate, boolean excludeLastFooter) {
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
        ConfigsDto configsDto = new Settings(settingsFacade).getConfigsDto();
        Config config;

        if (sms.hasConfig()) {
            config = configsDto.getConfig(sms.getConfig());
        }
        else {
            logger.info("No config specified, using default config.");
            config = configsDto.getDefaultConfig();
        }

        //todo: die if things aren't right, right?
        //todo: SMS_SCHEDULE_FUTURE_SMS research if any sms provider provides that, for now assume not.

        Integer maxSize = config.getMaxSmsSize();
        String header = config.getSplitHeader();
        String footer = config.getSplitFooter();
        Boolean excludeLastFooter = config.getExcludeLastFooter();
        Boolean isMultiRecipientSupported = config.getMultiRecipientSupport();

        // -2 to account for the added \n after the header and before the footer
        if ((maxSize - header.length() - footer.length() - 2) <= 0) {
            throw new IllegalArgumentException("The combined sizes of the header and footer templates are larger than the maximum SMS size!");
        }

        List<String> messageParts = splitMessage(sms.getMessage(), maxSize, header, footer, excludeLastFooter);
        logger.info("messageParts: {}", messageParts.toString().replace("\n", "\\n"));

        //todo: delivery_time
        if (isMultiRecipientSupported) {
            for (String part : messageParts) {
                if (sms.hasDeliveryTime()) {
                    RunOnceSchedulableJob schedulableJob = new RunOnceSchedulableJob(
                            SmsEvents.makeSendEvent(config.getName(), sms.getRecipients(), part),
                            sms.getDeliveryTime().toDate());
                    schedulerService.safeScheduleRunOnceJob(schedulableJob);
                    logger.info(String.format("Scheduling message [%s] to multiple recipients %s at %s.",
                            part.replace("\n", "\\n"), sms.getRecipients(), sms.getDeliveryTime()));
                }
                else {
                    eventRelay.sendEventMessage(SmsEvents.makeSendEvent(config.getName(), sms.getRecipients(), part));
                    logger.info("Sending message [{}] to multiple recipients {}.", part.replace("\n", "\\n"),
                            sms.getRecipients());
                }
            }
        } else {
            for (String recipient : sms.getRecipients()) {
                for (String part : messageParts) {
                    if (sms.hasDeliveryTime()) {
                        RunOnceSchedulableJob schedulableJob = new RunOnceSchedulableJob(
                                SmsEvents.makeSendEvent(config.getName(), Arrays.asList(recipient), part),
                                sms.getDeliveryTime().toDate());
                        schedulerService.safeScheduleRunOnceJob(schedulableJob);
                        logger.info(String.format("Scheduling message [%s] to one recipient %s at %s.",
                                part.replace("\n", "\\n"), sms.getRecipients(), sms.getDeliveryTime()));
                    }
                    else {
                        logger.info("Sending message [{}] to one recipient {}.", part.replace("\n", "\\n"), recipient);
                        eventRelay.sendEventMessage(SmsEvents.makeSendEvent(config.getName(), Arrays.asList(recipient),
                                part));
                    }
                }
            }
        }
    }
}