package org.motechproject.sms.service;

import org.motechproject.sms.model.OutgoingSms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("smsSenderService")
public class SmsServiceImpl implements SmsService {

    private static Logger log = LoggerFactory.getLogger(SmsService.class);

    @Override
    /**
     * TODO
     */
    public void send(final OutgoingSms outgoingSms){
        log.info("Sending SMS:" + outgoingSms.toString());

        //throw new SmsDeliveryFailureException("Hello, world!");

        //TODO: send sms here!
    }
}
