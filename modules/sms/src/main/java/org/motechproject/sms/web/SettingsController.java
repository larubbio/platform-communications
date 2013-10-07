package org.motechproject.sms.web;

import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.constants.Defaults;
import org.motechproject.sms.settings.Settings;
import org.motechproject.sms.settings.ConfigsDto;
import org.motechproject.sms.templates.Template;
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
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
        List<Template> templates = templateReader.getTemplates();
        Map<String, TemplateForWeb> ret = new HashMap<String, TemplateForWeb>();
        for (Template template : templates) {
            ret.put(template.getName(), new TemplateForWeb(template));
        }
        return ret;
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
    public ConfigsDto getConfigs() {
        Settings settings = new Settings(settingsFacade);
        return settings.getConfigsDto();
    }

    @RequestMapping(value = "/configs", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ConfigsDto setConfigs(@RequestBody ConfigsDto configsDto) {
        Settings settings = new Settings(settingsFacade);
        settings.setConfigsDto(configsDto);
        return settings.getConfigsDto();
    }

    //todo: since configs are validated client-side, do we need that?
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        return e.getMessage();
    }
}
