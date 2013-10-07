package org.motechproject.sms.model;

import com.google.gson.JsonIOException;
import org.motechproject.server.config.SettingsFacade;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class Templates {
    private SettingsFacade settingsFacade;
    private Map<String, Properties> templates;

    public Templates(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
        //todo: basic template validation? [like bad HttpMethod, for example]
        //todo: some kind of template validator for 'hand-made' user provided templates?
        try {
            templates = settingsFacade.getAllProperties(settingsFacade.getSymbolicName());
        } catch (IOException e) {
            //todo: what do i really want to do here?
            throw new JsonIOException(e);
        }
    }

    public Map<String, Properties> getTemplates() {
        return templates;
    }

    public Template getTemplate(String name) {
        Template template = new Template(templates.get(name));
        return template;
    }
}
