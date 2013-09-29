package org.motechproject.sms.web;

import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.model.Configs;
import org.motechproject.sms.model.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@Controller
public class SettingsController {
    private SettingsFacade settingsFacade;
    private Settings settings;

    @Autowired
    public SettingsController(@Qualifier("smsSettings") final SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
        this.settings = new Settings(settingsFacade);
    }

    @RequestMapping(value = "/configs", method = RequestMethod.GET)
    @ResponseBody
    public Configs getConfigs() {
        return settings.getConfigs();
    }

    @RequestMapping(value = "/templates", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Properties> getTemplates() {
        return settings.getTemplates();
    }

    @RequestMapping(value = "/configs", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void setSettings(Configs configs) {
        StringBuilder exceptionMessage = new StringBuilder();

        if (1 == 0) {
            exceptionMessage
                    .append(String.format("format", "data"))
                    .append("\n");
        }

        if (exceptionMessage.length() > 0) {
            throw new IllegalStateException(exceptionMessage.toString());
        }

        settings.setConfigs(settingsFacade, configs);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        return e.getMessage();
    }
}
