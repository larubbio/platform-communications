package org.motechproject.sms.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.*;

/**
 * TODO
 *
 */
public class Configs {
    private List<Map<String, String>> configs;
    private List<String> errors;

    public Configs() {
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

    public void clearErrors() {
        this.errors = new ArrayList<String>();
    }

    private void addError(String error) {
        errors.add(error);
    }

    //todo: duplicate names & multiple defaults
    public void validate(Map<String, Properties> templates) {
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
            if (!config.containsKey("template") || config.get("template").length() < 1)
            {
                addError("The following config was ignored because it has no template: " + config.toString());
                i.remove();
                continue;
            }
            if (!templates.containsKey(config.get("template"))) {
                addError("The following config was ignored because its template (" + config.get("template") + ") doesn't exist on this system.");
                i.remove();
                continue;
            }
            if (firstConfigName == null) {
                firstConfigName = config.get("name");
            }
        }
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

        final Configs other = (Configs) obj;

        return compareFields(other);
    }

    @Override
    public String toString() {
        return String.format("Configs{configs='%s'}", configs);
    }

    private Boolean compareFields(Configs other) {
        if (!Objects.equals(this.configs, other.configs)) {
            return false;
        }
        return true;
    }
}
