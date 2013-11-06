package org.motechproject.sms.event;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.sms.service.OutgoingSms;
import org.motechproject.sms.http.SmsHttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * When another module sends an SMS, it calls SmsService.send, which in turn sends one or more SEND_SMS events which
 * are handled here and passed straight through to to SmsHttpService.send
 */
@Service
public class SendSmsEventHandler {

    private SmsHttpService smsHttpService;
    private Logger logger = LoggerFactory.getLogger(SendSmsEventHandler.class);

    @Autowired
    public SendSmsEventHandler(SmsHttpService smsHttpService) {
        this.smsHttpService = smsHttpService;
    }

    @MotechListener (subjects = { SmsEvents.OUTBOUND_SMS_PENDING, SmsEvents.OUTBOUND_SMS_SCHEDULED,
            SmsEvents.OUTBOUND_SMS_RETRYING })
    public void handle(MotechEvent event) {
        logger.info("Handling {}: {}", event.getSubject(),
            event.getParameters().get("message").toString().replace("\n", "\\n"));
        smsHttpService.send(new OutgoingSms(event));
    }
}

