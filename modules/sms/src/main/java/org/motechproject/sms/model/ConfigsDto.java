package org.motechproject.sms.model;

import java.util.List;

/**
 * todo
 */
public class ConfigsDto {
    private String defaultConfig;
    private List<Config> configs;

    public ConfigsDto() {
    }

    public String getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(String defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public List<Config> getConfigs() {
        return configs;
    }

    public void setConfigs(List<Config> configs) {
        this.configs = configs;
    }

    public Config getConfig(String name) {
        for(Config config : configs) {
            if (config.getName() == name) {
                return config;
            }
        }
        throw new IllegalArgumentException("'" + name + "': no such config");
    }
}
