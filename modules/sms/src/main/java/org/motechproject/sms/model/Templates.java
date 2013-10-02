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
        try {
            templates = (Map<String, Properties>)settingsFacade.getAllProperties(settingsFacade.getSymbolicName());
        } catch (IOException e) {
            //todo: what do i really want to do here?
            throw new JsonIOException(e);
        }
    }

    public Map<String, Properties> getTemplates() {
        return templates;
    }
}
