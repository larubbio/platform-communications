package org.motechproject.sms.service.impl;

import org.motechproject.sms.constants.SendSmsConstants;
import org.motechproject.sms.model.Sms;
import org.motechproject.sms.service.SmsSenderService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SendSmsEventHandlerImpl {

    @Autowired
    private SmsSenderService smsSenderService;

    @MotechListener (subjects = { SendSmsConstants.SEND_SMS})
    public void handle(MotechEvent event) {
        List<String> recipients = (List<String>) event.getParameters().get(SendSmsConstants.RECIPIENTS);
        String message = (String) event.getParameters().get(SendSmsConstants.MESSAGE);
        String from = (String) event.getParameters().get(SendSmsConstants.AT);

        smsSenderService.send(new Sms(from, to, message));
    }
}

