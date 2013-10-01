package org.motechproject.sms.model;

import com.google.gson.JsonIOException;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * todo
 */
public class Templates {
    private Map<String, Properties> templates = new HashMap<String, Properties>();

    public Templates(@Qualifier("smsSettings") SettingsFacade settingsFacade) {
        //
        // templates
        //

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
}
