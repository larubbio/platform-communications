package org.motechproject.sms.service;

import org.motechproject.sms.service.SmsConfigService;
import org.motechproject.sms.service.SmsSenderService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
@Service("smsConfigService")
public class SmsConfigServiceImpl implements SmsConfigService {

    @Override
    public List<String> getConfigs() {
        List<String> ret = new ArrayList<String>();

        //TODO
        ret.add("ClickaTell");
        ret.add("Nuntium");
        ret.add("Voxeo");

        return ret;
    }

}
