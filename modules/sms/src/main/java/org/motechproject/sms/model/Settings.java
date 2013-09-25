package org.motechproject.sms.model;

import java.util.*;

/**
 * TODO
 *
 */
public class Settings {
    private String defaultConfig;
    private List<Map<String, String>> configs;
    private List<String> errors;

    public Settings() {
        errors = new ArrayList<String>();
    }

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

    public List<String> getErrors() {
        return errors;
    }

    private void addError(String error) {
        errors.add(error);
    }

    public void validate() {
        String firstConfigName = null;
        Iterator<Map<String, String>> i = configs.iterator();
        while(i.hasNext())
        {
            Map<String, String> config = i.next();
            if (!config.containsKey("name") || config.get("name").length() < 1)
            {
                addError("The following config was ignored because it has no name: " + config.toString());
                i.remove();
                continue;
            }
            if (firstConfigName == null) {
                firstConfigName = config.get("name");
            }
        }

        if (defaultConfig != null && defaultConfig.length() > 0) {
            boolean validDefault = false;
            for (Map<String, String> config : configs)
            {
                if (config.get("name") == defaultConfig)
                {
                    validDefault = true;
                    break;
                }
            }
            if (!validDefault) {
                addError("The specified defaultConfig '" + defaultConfig + "' was ignored because there is no config with this name, so '" + firstConfigName + "' was chosen as the default config");
                setDefaultConfig(firstConfigName);
            }
        }
        else {
            addError("defaultConfig was not specified, so '" + firstConfigName + "' was chosen as the default config");
            setDefaultConfig(firstConfigName);
        }

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
