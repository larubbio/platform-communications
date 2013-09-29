package org.motechproject.sms.model;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import org.apache.commons.io.IOUtils;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class Settings {
    public static final String SMS_CONFIGS_FILE_NAME = "sms-configs.json";
    private static final Logger logger = LoggerFactory.getLogger(Settings.class);

    private Configs configs;
    private Map<String, Properties> templates = new HashMap<String, Properties>();

    @Autowired
    public Settings(@Qualifier("smsSettings") SettingsFacade settingsFacade) {

        //
        // templates
        //

        try {
            templates = settingsFacade.getAllProperties(settingsFacade.getSymbolicName());
        } catch (IOException e) {
            //todo: what do i really want to do here?
            throw new JsonIOException(e);
        }
        logger.debug("Loaded the following templates:" + this.templates.toString());

        //
        // configs
        //

        InputStream is = settingsFacade.getRawConfig(SMS_CONFIGS_FILE_NAME);
        try {
            String jsonText = IOUtils.toString(is);
            Gson gson = new Gson();
            this.configs = gson.fromJson(jsonText, Configs.class);
        } catch (IOException e) {
            throw new JsonIOException(e);
        }
        this.configs.validate(templates);
        logger.debug("Loaded the following configs:" + this.configs.toString());
    }

    public Configs getConfigs() {
        return configs;
    }

    public void setConfigs(SettingsFacade settingsFacade, Configs configs) {

        //todo: validate configs here ?

        this.configs = configs;

        Gson gson = new Gson();
        String jsonText = gson.toJson(configs, Configs.class);
        ByteArrayResource resource = new ByteArrayResource(jsonText.getBytes());
        settingsFacade.saveRawConfig(SMS_CONFIGS_FILE_NAME, resource);
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

        final Settings other = (Settings) obj;

        return compareFields(other);
    }

    @Override
    public String toString() {
        return String.format("Settings{configs='%s'}", configs);
    }

    private Boolean compareFields(Settings other) {
        if (!Objects.equals(this.configs, other.configs)) {
            return false;
        }

        return true;
    }
}
