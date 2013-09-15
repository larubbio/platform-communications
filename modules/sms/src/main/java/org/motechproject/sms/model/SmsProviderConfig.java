package org.motechproject.sms.model;

/**
 * TODO
 *
 */
public class SmsProviderConfig {
    private String name;
    private String template;

    public SmsProviderConfig(String name, String template) {
        this.name = name;
        this.template = template;
    }

    public SmsProviderConfig() {
        this(null, null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
