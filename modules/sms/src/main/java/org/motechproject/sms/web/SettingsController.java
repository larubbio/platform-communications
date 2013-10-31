package org.motechproject.sms.web;

import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.configs.ConfigReader;
import org.motechproject.sms.configs.Configs;
import org.motechproject.sms.templates.TemplateForWeb;
import org.motechproject.sms.templates.TemplateReader;
import org.motechproject.sms.templates.Templates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

//todo: find a way to report useful information if encountering malformed templates?

/**
 * Sends templates to the UI, sends & received configs to/from the UI.
 */
@Controller
public class SettingsController {
    private SettingsFacade settingsFacade;
    private TemplateReader templateReader;

    @Autowired
    public SettingsController(@Qualifier("smsSettings") SettingsFacade settingsFacade, TemplateReader templateReader) {
        this.settingsFacade = settingsFacade;
        this.templateReader = templateReader;
    }

    @RequestMapping(value = "/templates", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, TemplateForWeb> getTemplates() {
        Templates templates = templateReader.getTemplates();
        return templates.templatesForWeb();
    }

    @RequestMapping(value = "/configs", method = RequestMethod.GET)
    @ResponseBody
    public Configs getConfigs() {
        ConfigReader configReader = new ConfigReader(settingsFacade);
        return configReader.getConfigs();
    }

    @RequestMapping(value = "/configs", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Configs setConfigs(@RequestBody Configs configs) {
        ConfigReader configReader = new ConfigReader(settingsFacade);
        configReader.setConfigs(configs);
        return configReader.getConfigs();
    }

    //todo: since configs are validated client-side, do we need that?
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        return e.getMessage();
    }
}
