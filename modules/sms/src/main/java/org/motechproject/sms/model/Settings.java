package org.motechproject.sms.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * TODO
 *
 */
public class Settings {
    private String defaultConfig;
    private List<Map<String, String>> configs;

    public List<Map<String, String>> getConfigs() {
        return configs;
    }

    public void setConfigs(List<Map<String, String>> configs) {
        this.configs = configs;
    }

    public String getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(String defaultConfig) {
        for (Map<String, String> config : configs)
        {
            if (config.containsKey("name") && config.get("name") == defaultConfig)
            {
                this.defaultConfig = defaultConfig;
                return;
            }
        }
        throw new IllegalArgumentException("Provided default key doesn't correspond to an existing configuration.");
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultConfig, configs);
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
        return String.format("Settings{defaultConfig='%s', configs='%s'}", defaultConfig, configs);
    }

    private Boolean compareFields(Settings other) {
        if (!Objects.equals(this.defaultConfig, other.defaultConfig)) {
            return false;
        }
        else if (!Objects.equals(this.configs, other.configs)) {
            return false;
        }
        return true;
    }
}
