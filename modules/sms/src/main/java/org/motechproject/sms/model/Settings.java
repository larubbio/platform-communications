package org.motechproject.sms.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * TODO
 *
 */
public class Settings {
    private String defCfg;
    private List<Map<String, String>> configs;

    public List<Map<String, String>> getConfigs() {
        return configs;
    }

    public void setConfigs(List<Map<String, String>> configs) {
        this.configs = configs;
    }

    public String getDefaultConfig() {
        return defCfg;
    }

    public void setDefaultConfig(String defaultConfig) {
        for (Map<String, String> config : configs)
        {
            if (config.containsKey("name") && config.get("name") == defaultConfig)
            {
                this.defCfg = defaultConfig;
                return;
            }
        }
        throw new IllegalArgumentException("Provided default key doesn't correspond to an existing configuration.");
    }

    @Override
    public int hashCode() {
        return Objects.hash(defCfg, configs);
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
        return String.format("Settings{defCfg='%s', configs='%s'}", defCfg, configs);
    }

    private Boolean compareFields(Settings other) {
        if (!Objects.equals(this.defCfg, other.defCfg)) {
            return false;
        }
        else if (!Objects.equals(this.configs, other.configs)) {
            return false;
        }
        return true;
    }
}
