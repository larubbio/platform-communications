package org.motechproject.sms.sms;

import org.motechproject.sms.model.OutgoingSms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsSenderService {

    private Logger logger = LoggerFactory.getLogger(SmsSenderService.class);

    public void send(OutgoingSms sms) {
        logger.info("Actually sending {}", sms.toString());
    }
}
