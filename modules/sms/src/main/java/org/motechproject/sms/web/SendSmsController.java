package org.motechproject.sms.web;

import org.motechproject.sms.model.OutgoingSms;
import org.motechproject.sms.service.SmsSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@Controller
public class SendSmsController {
    private SmsSenderService senderService;

    @Autowired
    public SendSmsController(SmsSenderService senderService) {
        this.senderService = senderService;
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void sendSms(@RequestBody OutgoingSms outgoingSms) throws Exception {
        //todo: kill thr 'throws' above
        senderService.send(outgoingSms);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        return e.getMessage();
    }
}
