package org.motechproject.sms.settings;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import org.apache.commons.io.IOUtils;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.core.io.ByteArrayResource;

import java.io.InputStream;

public class Settings {
    public static final String SMS_SETTINGS_FILE_NAME = "sms-settings.json";
    private SettingsFacade settingsFacade;

    public Settings(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

    public ConfigsDto getConfigsDto() {
        ConfigsDto configsDto;
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
        return configsDto;
    }

    public void setConfigsDto(ConfigsDto configsDto) {

        //todo: validate settingsDto here ?

        Gson gson = new Gson();
        //Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
        String jsonText = gson.toJson(configsDto, ConfigsDto.class);
        ByteArrayResource resource = new ByteArrayResource(jsonText.getBytes());
        settingsFacade.saveRawConfig(SMS_SETTINGS_FILE_NAME, resource);
    }
}
