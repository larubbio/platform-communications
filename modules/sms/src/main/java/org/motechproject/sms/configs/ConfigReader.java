package org.motechproject.sms.configs;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import org.apache.commons.io.IOUtils;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.core.io.ByteArrayResource;

import java.io.InputStream;

public class ConfigReader {
    public static final String SMS_CONFIGS_FILE_NAME = "sms-configs.json";
    private SettingsFacade settingsFacade;

    public ConfigReader(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

    public Configs getConfigs() {
        Configs configs;
        InputStream is = settingsFacade.getRawConfig(SMS_CONFIGS_FILE_NAME);
        if (is == null) {
            throw new JsonIOException(SMS_CONFIGS_FILE_NAME + " missing");
        }
        try {
            String jsonText = IOUtils.toString(is);
            Gson gson = new Gson();
            //Type type = new TypeToken<Map<String, Config>>() {}.getType();
            configs = gson.fromJson(jsonText, Configs.class);
        } catch (Exception e) {
            //todo: what do we do with these? (might be coming from malformed .json config file)
            throw new JsonIOException("Might you have a malformed " + SMS_CONFIGS_FILE_NAME + " file? " + e.toString());
        }
        return configs;
    }

    public void setConfigs(Configs configs) {

        //todo: validate settingsDto here ?

        Gson gson = new Gson();
        //Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
        String jsonText = gson.toJson(configs, Configs.class);
        ByteArrayResource resource = new ByteArrayResource(jsonText.getBytes());
        settingsFacade.saveRawConfig(SMS_CONFIGS_FILE_NAME, resource);
    }
}
