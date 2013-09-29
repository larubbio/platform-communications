package org.motechproject.sms.service;


import java.util.List;


public interface SmsConfigService {

    List<String> getConfigs();
    String getDefaultConfig();
}
