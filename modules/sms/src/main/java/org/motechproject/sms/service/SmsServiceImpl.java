package org.motechproject.sms.service;

import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.constants.Defaults;
import org.motechproject.sms.event.SendSmsEvent;
import org.motechproject.sms.model.Configs;
import org.motechproject.sms.model.OutgoingSms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service("smsService")
public class SmsServiceImpl implements SmsService {

    private SettingsFacade settingsFacade;
    private Logger logger = LoggerFactory.getLogger(SmsService.class);
    private EventRelay eventRelay;
    private MotechSchedulerService schedulerService;


    @Autowired
    public SmsServiceImpl(@Qualifier("smsSettings") final SettingsFacade settingsFacade, EventRelay eventRelay, MotechSchedulerService schedulerService) {
        //todo: persist settings or reload them for each call?
        //todo: right now I'm doing the latter...
        //todo: ... but I'm not wed to it.
        this.settingsFacade = settingsFacade;
        this.eventRelay = eventRelay;
        this.schedulerService = schedulerService;
    }

    protected String emptyPropOrVal(Map<String, String> map, String key, String val) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return val;
    }

    static private String propOrVal(Map<String, String> map, String key, String val) {
        if (map.containsKey(key) && (map.get(key).length() > 0)) {
            return map.get(key);
        }
        return val;
    }

     static private List<String> splitMessage(String message, int maxSize, String headerTemplate, String footerTemplate, boolean excludeLastFooter) {

        List<String> parts = new ArrayList<String>();
        int messageLength = message.length();

        if (messageLength <= maxSize) {
            parts.add(message);
        } else {
            //NOTE: nifty trick: since the format placeholders $m and $t are two characters wide and we assume no more
            //than 99 parts, we don't need to do a String.format() to figure out the length of the actual header/footer
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
    public void send(final OutgoingSms outgoingSms){
        Configs configs = new Configs(settingsFacade);
        String configName = outgoingSms.getConfig();
        Map<String, String> config;

        if (configName == null) {
            logger.info("No config specified, using default config.");
            config = configs.getDefaultConfig();
        }
        else {
            config = configs.getConfig(configName);
        }

        //todo: die if things aren't right, right?
        //todo: SMS_SCHEDULE_FUTURE_SMS research if any sms provider provides that, for now assume not.

        Integer maxSize = Integer.parseInt(propOrVal(config, "max_sms_size", Defaults.MAX_SMS_SIZE)); //todo: what if it's an unparsable string?
        String header = emptyPropOrVal(config, "split_header", Defaults.SPLIT_HEADER);
        String footer = emptyPropOrVal(config, "split_footer", Defaults.SPLIT_FOOTER);
        Boolean excludeLastFooter = Boolean.parseBoolean(propOrVal(config, "split_footer", Defaults.SPLIT_EXCLUDE));
        Boolean isMultiRecipientSupported = Boolean.parseBoolean(propOrVal(config, "multi_recipient", Defaults.MULTI_RECIPIENT));

        // -2 to account for the added \n after and before the header & footer
        if ((maxSize - header.length() - footer.length() - 2) <= 0) {
            throw new IllegalArgumentException("The combined sizes of the header and footer templates are larger than the maximum SMS size!");
        }

        List<String> messageParts = splitMessage(outgoingSms.getMessage(), maxSize, header, footer, excludeLastFooter);
        logger.info("messageParts: {}", messageParts.toString().replace("\n", "\\n"));

        if (isMultiRecipientSupported) {
            for (String part : messageParts) {
                logger.info("Sending message [{}] to multiple recipients {}.", part, outgoingSms.getRecipients());
                eventRelay.sendEventMessage(new SendSmsEvent(outgoingSms.getRecipients(), part).toMotechEvent());
            }
        } else {
            for (String recipient : outgoingSms.getRecipients()) {
                for (String part : messageParts) {
                    logger.info("Sending message [{}] to one recipient {}.", part, recipient);
                    eventRelay.sendEventMessage(new SendSmsEvent(Arrays.asList(recipient), part).toMotechEvent());
                }
            }
        }
    }
}