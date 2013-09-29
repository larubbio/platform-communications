package org.motechproject.sms.model;

import com.google.gson.JsonIOException;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final Logger logger = LoggerFactory.getLogger(Settings.class);

    @Autowired
    public Templates(@Qualifier("smsTemplates") SettingsFacade settingsFacade) {

        //
        // templates
        //

        try {
            templates = settingsFacade.getAllProperties(settingsFacade.getSymbolicName());
        } catch (IOException e) {
            //todo: what do i really want to do here?
            throw new JsonIOException(e);
        }
        logger.debug("Loaded the following templates:" + this.templates.toString());
    }

    public Map<String, Properties> getTemplates() {
        return templates;
    }
}
