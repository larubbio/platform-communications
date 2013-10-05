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
    private ConfigsDto configsDto;

    public Configs(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
        InputStream is = settingsFacade.getRawConfig(SMS_SETTINGS_FILE_NAME);
        try {
            String jsonText = IOUtils.toString(is);
            Gson gson = new Gson();
            //Type type = new TypeToken<Map<String, Config>>() {}.getType();
            configsDto = gson.fromJson(jsonText, ConfigsDto.class);
        } catch (Exception e) {
            //todo: what do we do with these? (might be coming from malformed .json config file)
            throw new JsonIOException("Might you have a malformed " + SMS_SETTINGS_FILE_NAME + " file? " + e.toString());
        }
    }

    public ConfigsDto getConfigsDto() {
        return configsDto;
    }
/*
    public Config getConfig(String name) {
        if (configsDto.getConfigs().containsKey(name)) {
            return configsDto.getConfigs().get(name);
        }
        throw new IllegalArgumentException(String.format("Configuration '%s' does not exist.", name));
    }

    public Config getDefaultConfig() {
        try {
            return configsDto.getConfigs().get(configsDto.getDefaultConfig());
        }
        catch (Exception e) //todo: narrow that down, dude...
        {
            throw new IllegalStateException(String.format("No default configuration."));
        }
    }
*/
    public void setConfigs(List<Map<String, Object>> configs) {

        //todo: validate settingsDto here ?

        Gson gson = new Gson();
        //Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
        String jsonText = gson.toJson(configs, ConfigsDto.class);
        ByteArrayResource resource = new ByteArrayResource(jsonText.getBytes());
        settingsFacade.saveRawConfig(SMS_SETTINGS_FILE_NAME, resource);
    }
}
