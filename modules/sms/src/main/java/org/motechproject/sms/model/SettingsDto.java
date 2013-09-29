package org.motechproject.sms.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.*;

/**
 * TODO
 *
 */
public class SettingsDto {
    private List<Map<String, String>> configs;
    private List<String> errors;

    public SettingsDto() {
        errors = new ArrayList<String>();
    }

    public List<Map<String, String>> getConfigs() {
        return configs;
    }

    public void setConfigs(List<Map<String, String>> configs) {
        this.configs = configs;
    }

    @JsonIgnore
    public String getDefaultConfig() {
        String firstConfig = null;
        for (Map<String, String> config : configs)
        {
            if (config.get("default").equals("true")) {
                return config.get("name");
            }
            if (firstConfig == null) {
                firstConfig = config.get("name");
            }
        }
        return firstConfig;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    @JsonIgnore
    public void clearErrors() {
        this.errors = new ArrayList<String>();
    }

    @JsonIgnore
    public void addError(String error) {
        errors.add(error);
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
