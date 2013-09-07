package org.motechproject.sms.service.impl;

import org.joda.time.DateTime;
import org.motechproject.sms.model.OutgoingSms;
import org.motechproject.sms.service.SmsSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service("smsSenderService")
public class SmsSenderServiceImpl implements SmsSenderService {

    private static Logger log = LoggerFactory.getLogger(SmsSenderService.class);

    @Override
    /**
     * TODO
     */
    public void send(final OutgoingSms outgoingSms) throws Exception {

        log.info("Sending SMS:" + outgoingSms.toString());

        if (outgoingSms.getMessage().equals("fail")) {
            throw new Exception("Eeek! What happened?");
        }
        //TODO: send sms here!
    }

    @Override
    public void send(List<String> recipients, String message, DateTime deliveryTime) {
        try {
        send(new OutgoingSms(recipients, message, deliveryTime));
    }
        catch (Exception e) {
            //TODO: kill that, the throwing is just for fun
        }
    }

    @Override
    public void send(String recipient, String message, DateTime deliveryTime) {
        try {
            send(new OutgoingSms(Arrays.asList(new String[]{recipient}), message, deliveryTime));
        }
        catch (Exception e) {
            //TODO: kill that, the throwing is just for fun
        }
    }
}
