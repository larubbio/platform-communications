package org.motechproject.sms.web;

import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.constants.Defaults;
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
import java.util.HashMap;
import java.util.Map;

//todo: find a way to report useful information to implementers who drop in malformed templates

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

    @RequestMapping(value = "/defaults", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getDefaults() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("max_retries", Defaults.MAX_RETRIES);
        map.put("max_sms_size", Defaults.MAX_SMS_SIZE);
        map.put("split_header", Defaults.SPLIT_HEADER);
        map.put("split_footer", Defaults.SPLIT_FOOTER);
        map.put("split_exclude_last_footer", Defaults.SPLIT_EXCLUDE);
        map.put("multi_recipient", Defaults.MULTI_RECIPIENT);
        return map;
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
