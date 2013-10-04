package org.motechproject.sms.event;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.sms.constants.SendSmsConstants;
import org.motechproject.sms.model.OutgoingSms;
import org.motechproject.sms.sms.SmsSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SendSmsEventHandler {

    private SmsSenderService sender;
    private Logger logger = LoggerFactory.getLogger(SendSmsEventHandler.class);

    @Autowired
    public SendSmsEventHandler(SmsSenderService sender) {
        this.sender = sender;
    }

    @MotechListener (subjects = { SendSmsConstants.SEND_SMS })
    public void handle(MotechEvent event) {

        logger.info("handling {}", event.toString().replace("\n", "\\n"));

        Map<String, Object> params = event.getParameters();

        String config = (String) params.get(SendSmsConstants.CONFIG);
        List<String> recipients = (List<String>) params.get(SendSmsConstants.RECIPIENTS);
        String message = (String) params.get(SendSmsConstants.MESSAGE);
        DateTime deliveryTime = (DateTime) event.getParameters().get(SendSmsConstants.DELIVERY_TIME);

        sender.send(new OutgoingSms(config, recipients, message, deliveryTime));
    }
}

