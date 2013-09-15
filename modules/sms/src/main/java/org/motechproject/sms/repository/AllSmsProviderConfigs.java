package org.motechproject.sms.repository;

import com.google.gson.reflect.TypeToken;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.sms.model.SmsProviderConfig;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

@Component
public class AllSmsProviderConfigs {

    public static final String CONFIG_FILE_NAME = "sms.configs.json";

    private MotechJsonReader motechJsonReader;

    private SettingsFacade settings;

    @Autowired
    public AllSmsProviderConfigs(@Qualifier("smsSettings") SettingsFacade settings) {
        this.settings = settings;
        this.motechJsonReader = new MotechJsonReader();
    }

    public List<SmsProviderConfig> getAllConfigs() {
        Type type = new TypeToken<List<SmsProviderConfig>>() {
        }.getType();

        InputStream is = settings.getRawConfig(CONFIG_FILE_NAME);

        List<SmsProviderConfig> configs = (List<SmsProviderConfig>) motechJsonReader.readFromStream(is, type);

        return configs;
    }
}
