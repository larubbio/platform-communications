package org.motechproject.sms.model;

import java.util.Map;
import java.util.Objects;

/**
 * TODO
 *
 */
public class SmsSettings {
    private String defaultConfig;
    private Map<String, Map<String,String>> configs;

    public Map<String, Map<String,String>> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, Map<String,String>> configs) {
        this.configs = configs;
    }

    public String getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(String defaultConfig) {
        this.defaultConfig = defaultConfig;
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

        final SmsSettings other = (SmsSettings) obj;

        return compareFields(other);
    }

    @Override
    public String toString() {
        return String.format("SmsSettings{defaultConfig='%s', configs='%s'}", defaultConfig, configs);
    }

    private Boolean compareFields(SmsSettings other) {
        if (!Objects.equals(this.defaultConfig, other.defaultConfig)) {
            return false;
        }
        else if (!Objects.equals(this.configs, other.configs)) {
            return false;
        }
        return true;
    }
}
