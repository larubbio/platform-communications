package org.motechproject.sms.service;

import org.joda.time.DateTime;
import org.motechproject.sms.model.OutgoingSms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("smsSenderService")
public class SmsSenderServiceImpl implements SmsSenderService {

    private static Logger log = LoggerFactory.getLogger(SmsSenderService.class);

    @Override
    /**
     * TODO
     */
    public void send(final OutgoingSms outgoingSms){
        log.info("Sending SMS:" + outgoingSms.toString());

        //TODO: send sms here!
    }

    @Override
    public void send(List<String> recipients, String message) {

    }

    @Override
    public void send(List<String> recipients, String message, DateTime deliveryTime){

    }

    @Override
    public void send(String config, List<String> recipients, String message) {
        send(new OutgoingSms(config, recipients, message));
    }

    @Override
    public void send(String config, List<String> recipients, String message, DateTime deliveryTime) {
        send(new OutgoingSms(config, recipients, message, deliveryTime));
    }
}
