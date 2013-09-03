package org.motechproject.sms.service.impl;

import org.motechproject.sms.constants.SendSmsConstants;
import org.motechproject.sms.model.Sms;
import org.motechproject.sms.service.SmsSenderService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendSmsEventHandlerImpl {

    @Autowired
    private SmsSenderService smsSenderService;

    @MotechListener (subjects = { SendSmsConstants.SEND_SMS_SUBJECT })
    public void handle(MotechEvent event) {
        String fromAddress = (String) event.getParameters().get(SendSmsConstants.FROM_ADDRESS);
        String toAddress = (String) event.getParameters().get(SendSmsConstants.TO_ADDRESS);
        String subject = (String) event.getParameters().get(SendSmsConstants.SUBJECT);
        String message = (String) event.getParameters().get(SendSmsConstants.MESSAGE);

        smsSenderService.send(new Sms(fromAddress, toAddress, subject, message));
    }
}

