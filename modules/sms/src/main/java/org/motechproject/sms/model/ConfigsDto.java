package org.motechproject.sms.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.util.Map;

/**
 * todo
 */
public class ConfigsDto {
    private String defaultConfig;
    private Map<String, Config> configs;

    public ConfigsDto() {

    }

    public String getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(String defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public Map<String, Config> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, Config> configs) {
        this.configs = configs;
    }
}
