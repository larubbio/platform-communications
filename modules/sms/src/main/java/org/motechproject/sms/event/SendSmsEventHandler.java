package org.motechproject.sms.event;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.sms.service.OutgoingSms;
import org.motechproject.sms.http.SmsHttpService;
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

    @MotechListener (subjects = { SmsEvents.SEND_SMS })
    public void handle(MotechEvent event) {
        logger.info("Handling {}: {}", event.getSubject(),
            event.getParameters().get("message").toString().replace("\n", "\\n"));
        sender.send(new OutgoingSms(event));
    }
}
