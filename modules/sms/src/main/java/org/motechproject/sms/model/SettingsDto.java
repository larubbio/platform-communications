package org.motechproject.sms.model;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Objects;

public class SettingsDto {
    public static final String SMS_SETTINGS_FILE_NAME = "sms-config.json";
    public static final String SMS_SETTINGS = "settings";
    private static final Logger logger = LoggerFactory.getLogger(SettingsDto.class);

    private Settings settings;

    @Autowired
    public SettingsDto(@Qualifier("smsSettings") SettingsFacade settingsFacade) {
        MotechJsonReader motechJsonReader = new MotechJsonReader();
        Type type = new TypeToken<Object>() { }.getType();

        InputStream is = settingsFacade.getRawConfig(SMS_SETTINGS_FILE_NAME);

        try {
            String jsonText = IOUtils.toString(is);
            Gson gson = new Gson();
            this.settings = gson.fromJson(jsonText, Settings.class);
        } catch (IOException e) {
            throw new JsonIOException(e);
        }
        logger.debug("this.settings=" + this.settings.toString());
    }

    public Settings getSettings() {
        return settings;
    }

    @Override
    public int hashCode() {
        return Objects.hash(settings);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final SettingsDto other = (SettingsDto) obj;

        return compareFields(other);
    }

    @Override
    public String toString() {
        return String.format("SettingsDto{settings='%s'}", settings);
    }

    private Boolean compareFields(SettingsDto other) {
        if (!Objects.equals(this.settings, other.settings)) {
            return false;
        }

        return true;
    }
}
