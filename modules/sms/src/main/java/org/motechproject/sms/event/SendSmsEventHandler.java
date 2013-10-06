package org.motechproject.sms.event;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.constants.SendSmsEventConstants;
import org.motechproject.sms.model.OutgoingSms;
import org.motechproject.sms.service.SmsSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SendSmsEventHandler {

    private SettingsFacade settingsFacade;
    private SmsSenderService sender;
    private Logger logger = LoggerFactory.getLogger(SendSmsEventHandler.class);

    @Autowired
    public SendSmsEventHandler(@Qualifier("smsSettings") SettingsFacade settingsFacade, SmsSenderService sender) {
        this.settingsFacade = settingsFacade;
        this.sender = sender;
    }

    @MotechListener (subjects = { SendSmsEventConstants.SEND_SMS })
    public void handle(MotechEvent event) {
        logger.info("handling {}", event.toString().replace("\n", "\\n"));

        Map<String, Object> params = event.getParameters();
        List<String> recipients = (List<String>) params.get(SendSmsEventConstants.RECIPIENTS);
        String message = (String) params.get(SendSmsEventConstants.MESSAGE);
        DateTime deliveryTime = (DateTime) params.get(SendSmsEventConstants.DELIVERY_TIME);
        String configName = (String) params.get(SendSmsEventConstants.CONFIG);

        sender.send(new OutgoingSms(configName, recipients, message, deliveryTime));
    }
}

