package org.motechproject.sms.model;

import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 */
public class SmsProviderConfigTemplate {
    private String name;
    private List<Map<String, String>> attrs;

    public SmsProviderConfigTemplate(String name) {
        this.name = name;
    }

    public SmsProviderConfigTemplate() {
        this(null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Map<String, String>> getAttrs() {
        return attrs;
    }
}
