package org.motechproject.sms.service;

import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.model.Configs;
import org.motechproject.sms.model.OutgoingSms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("smsService")
public class SmsServiceImpl implements SmsService {

    private Configs configs;
    private static Logger logger = LoggerFactory.getLogger(SmsService.class);

    @Autowired
    public SmsServiceImpl(@Qualifier("smsSettings") final SettingsFacade settingsFacade) {
        configs = new Configs(settingsFacade);
    }

    @Override
    /**
     * TODO
     */
    public void send(final OutgoingSms outgoingSms){
        String configName = outgoingSms.getConfig();
        Map<String, String> config;

        if (configName == null) {
            logger.info("No config specified, using default config.");
            config = configs.getDefaultConfig();
        }
        else {
            config = configs.getConfig(configName);
        }

        logger.info("Using config: " + config.get("name"));

        // Chunk message if it's too large


        //TODO: send sms here!
    }
}
