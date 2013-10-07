package org.motechproject.sms.event;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.sms.constants.SendSmsEventConstants;
import org.motechproject.sms.settings.OutgoingSms;
import org.motechproject.sms.service.SmsHttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendSmsEventHandler {

    private SmsHttpService sender;
    private Logger logger = LoggerFactory.getLogger(SendSmsEventHandler.class);

    @Autowired
    public SendSmsEventHandler(SmsHttpService sender) {
        this.sender = sender;
    }

    @MotechListener (subjects = { SendSmsEventConstants.SEND_SMS })
    public void handle(MotechEvent event) {
        logger.debug("handling {}", event.toString().replace("\n", "\\n"));

        sender.send(new OutgoingSms(event));
    }
}

