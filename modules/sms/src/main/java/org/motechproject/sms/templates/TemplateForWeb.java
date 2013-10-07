package org.motechproject.sms.templates;

/**
 * todo
 */
public class TemplateForWeb {
    private String name;

    public TemplateForWeb(Template template) {
        setName(template.getName());
        //todo: real work
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
