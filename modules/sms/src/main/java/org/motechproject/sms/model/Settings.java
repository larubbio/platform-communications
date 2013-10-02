package org.motechproject.sms.model;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import org.apache.commons.io.IOUtils;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Settings {
    public static final String SMS_SETTINGS_FILE_NAME = "sms-settings.json";
    // keep SMS_DEFAULT_RETRY in sync with sms.settings.retry.default in messages.properties
    public static final Integer SMS_DEFAULT_RETRY = 3; // Default maximum retries
    private static final Logger logger = LoggerFactory.getLogger(Settings.class);
    private SettingsDto settingsDto;

    public Settings(SettingsFacade settingsFacade) {
        Templates templates = new Templates(settingsFacade);
        InputStream is = settingsFacade.getRawConfig(SMS_SETTINGS_FILE_NAME);
        try {
            String jsonText = IOUtils.toString(is);
            Gson gson = new Gson();
            this.settingsDto = gson.fromJson(jsonText, SettingsDto.class);
        } catch (IOException e) {
            throw new JsonIOException(e);
        }
        validateConfigs(templates.getTemplates());
        logger.debug("Loaded the following settingsDto:" + this.settingsDto.toString());
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }

    //todo: check for duplicate names & multiple defaults
    //todo: only check 'newly dropped
    private void validateConfigs(Map<String, Properties> templates) {
        List<Map<String, String>> configs = settingsDto.getConfigs();
        String firstConfigName = null;
        Iterator<Map<String, String>> i = configs.iterator();
        while(i.hasNext())
        {
            Map<String, String> config = i.next();
            if (!config.containsKey("name") || config.get("name").length() < 1)
            {
                settingsDto.addError("The following config was ignored because it has no name: " + config.toString());
                i.remove();
                continue;
            }
            if (!config.containsKey("retry"))
            {
                //todo: resourcize/localize all
                settingsDto.addError(String.format("No Retry Count for %s config, the default Retry Count value %d will be used. ", config.get("name"), SMS_DEFAULT_RETRY));
                config.put("retry", SMS_DEFAULT_RETRY.toString());
            }
            else if (!isInteger(config.get("retry")))
            {
                settingsDto.addError(String.format("Invalid Retry Count for %s config, the default Retry Count value %d will be used.", config.get("name"), SMS_DEFAULT_RETRY));
                config.put("retry", SMS_DEFAULT_RETRY.toString());
            }
            if (!config.containsKey("template") || config.get("template").length() < 1)
            {
                settingsDto.addError("The following config was ignored because it has no template: " + config.toString());
                i.remove();
                continue;
            }
            if (!templates.containsKey(config.get("template"))) {
                settingsDto.addError("The following config was ignored because its template (" + config.get("template") + ") doesn't exist on this system.");
                i.remove();
                continue;
            }
            if (firstConfigName == null) {
                firstConfigName = config.get("name");
            }
        }
    }

    public SettingsDto getSettingsDto() {
        return settingsDto;
    }

    public void setConfigs(SettingsFacade settingsFacade, SettingsDto settingsDto) {

        //todo: validate settingsDto here ?

        this.settingsDto = settingsDto;

        Gson gson = new Gson();
        String jsonText = gson.toJson(settingsDto, SettingsDto.class);
        ByteArrayResource resource = new ByteArrayResource(jsonText.getBytes());
        settingsFacade.saveRawConfig(SMS_SETTINGS_FILE_NAME, resource);
    }
}
