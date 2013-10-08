package org.motechproject.sms.templates;

import java.util.List;

/**
 * todo
 */
public class TemplateForWeb {
    private String name;
    private List<String> requires;

    public TemplateForWeb(Template template) {
        setName(template.getName());
        setRequires(template.getRequires());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRequires() {
        return requires;
    }

    public void setRequires(List<String> requires) {
        this.requires = requires;
    }
}
