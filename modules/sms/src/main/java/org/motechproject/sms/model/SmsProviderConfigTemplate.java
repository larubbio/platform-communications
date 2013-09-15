package org.motechproject.sms.model;

/**
 * TODO
 *
 */
public class SmsProviderConfigTemplate {
    private String name;

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
}
