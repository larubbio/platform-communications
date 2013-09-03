package org.motechproject.sms.service.impl;

import org.motechproject.sms.model.Sms;
import org.motechproject.sms.service.SmsSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("smsSenderService")
public class SmsSenderServiceImpl implements SmsSenderService {


    @Override
    public void send(final Sms sms) {
        //send sms here!
    }
}
