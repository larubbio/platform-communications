package org.motechproject.sms.service;

import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.model.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO
 */
@Service("smsConfigService")
public class SmsConfigServiceImpl implements SmsConfigService {
    private Settings settings;
    private static final Logger logger = LoggerFactory.getLogger(SmsConfigServiceImpl.class);

    @Autowired
    public SmsConfigServiceImpl(@Qualifier("smsSettings") final SettingsFacade settingsFacade) {
        logger.info("Initializing SmsConfigServiceImpl");
        settings = new Settings(settingsFacade);
    }

    @Override
    public List<String> getConfigs() {
        List<String> ret = new ArrayList<String>();

        for (Map<String, String> config : settings.getSettingsDto().getConfigs()) {
            ret.add(config.get("name"));
        }

        return ret;
    }

    @Override
    public String getDefaultConfig() {
        return settings.getSettingsDto().getDefaultConfig();
    }
}
