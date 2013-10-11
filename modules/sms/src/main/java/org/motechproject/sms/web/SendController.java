package org.motechproject.sms.web;

import org.motechproject.sms.service.SmsService;
import org.motechproject.sms.service.OutgoingSms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class SendController {
    private SmsService smsService;

    @Autowired
    public SendController(SmsService smsService) {
        this.smsService = smsService;
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String sendSms(@RequestBody OutgoingSms outgoingSms) {
        smsService.send(outgoingSms);
        return String.format("The SMS to %s via the %s SMS provider was added to the message queue.", outgoingSms.getRecipients().toString(), outgoingSms.getConfig());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        String ret = e.getMessage();
        return ret;
    }
}
