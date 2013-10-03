package org.motechproject.sms.model;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.core.io.ByteArrayResource;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class Configs {
    public static final String SMS_SETTINGS_FILE_NAME = "sms-settings.json";
    private SettingsFacade settingsFacade;
    private List<Map<String, String>> configs;

    public Configs(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
        InputStream is = settingsFacade.getRawConfig(SMS_SETTINGS_FILE_NAME);
        try {
            String jsonText = IOUtils.toString(is);
            Gson gson = new Gson();
            Type type = new TypeToken<List<Map<String, String>>>() {}.getType();
            configs = gson.fromJson(jsonText, type);
        } catch (Exception e) {
            //todo: what do we do with these? (might be coming from malformed .json config file)
            throw new JsonIOException(e);
        }
    }

    public List<Map<String, String>> getConfigs() {
        return configs;
    }

    public void setConfigs(List<Map<String, String>> configs) {

        //todo: validate settingsDto here ?

        Gson gson = new Gson();
        Type type = new TypeToken<List<Map<String, String>>>() {}.getType();
        String jsonText = gson.toJson(configs, type);
        ByteArrayResource resource = new ByteArrayResource(jsonText.getBytes());
        settingsFacade.saveRawConfig(SMS_SETTINGS_FILE_NAME, resource);
    }
}
