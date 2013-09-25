package org.motechproject.sms.model;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import org.apache.commons.io.IOUtils;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SettingsDto {
    public static final String SMS_CONFIGS_FILE_NAME = "sms-configs.json";
    private static final Logger logger = LoggerFactory.getLogger(SettingsDto.class);

    private Configs configs;
    private Map<String, Properties> templates = new HashMap<String, Properties>();

    @Autowired
    public SettingsDto(@Qualifier("smsSettings") SettingsFacade settingsFacade) {

        try {
            templates = settingsFacade.getAllProperties(settingsFacade.getSymbolicName());
        } catch (IOException e) {
            //todo: what do i really want to do here?
            throw new JsonIOException(e);
        }

        InputStream is = settingsFacade.getRawConfig(SMS_CONFIGS_FILE_NAME);

        try {
            String jsonText = IOUtils.toString(is);
            Gson gson = new Gson();
            this.configs = gson.fromJson(jsonText, Configs.class);
        } catch (IOException e) {
            throw new JsonIOException(e);
        }

        this.configs.validate(templates);

        logger.debug("Loaded the following templates:" + this.templates.toString());
        logger.debug("Loaded the following configs:" + this.configs.toString());
    }

    public Configs getConfigs() {
        return configs;
    }

    public Map<String, Properties> getTemplates() {
        return templates;
    }

    @Override
    public int hashCode() {
        return Objects.hash(configs);
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
        return String.format("SettingsDto{configs='%s'}", configs);
    }

    private Boolean compareFields(SettingsDto other) {
        if (!Objects.equals(this.configs, other.configs)) {
            return false;
        }

        return true;
    }
}
