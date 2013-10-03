package org.motechproject.sms.web;

import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.constants.Defaults;
import org.motechproject.sms.model.Configs;
import org.motechproject.sms.model.Templates;
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

@Controller
public class SettingsController {
    private SettingsFacade settingsFacade;

    @Autowired
    public SettingsController(@Qualifier("smsSettings") final SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

    @RequestMapping(value = "/templates", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Properties> getTemplates() {
        Templates templates = new Templates(settingsFacade);
        return templates.getTemplates();
    }

    @RequestMapping(value = "/defaults", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getDefaults() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("max_retries", Defaults.MAX_RETRIES);
        map.put("max_sms_size", Defaults.MAX_SMS_SIZE);
        map.put("split_header", Defaults.SPLIT_HEADER);
        return map;
    }

    @RequestMapping(value = "/configs", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, String>> getConfigs() {
        Configs configs = new Configs(settingsFacade);
        return configs.getConfigs();
    }

    @RequestMapping(value = "/configs", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Map<String, String>> setConfigs(@RequestBody List<Map<String, String>> configs) {
        Configs settings = new Configs(settingsFacade);
        settings.setConfigs(configs);
        // reload settings
        settings = new Configs(settingsFacade);
        return settings.getConfigs();
    }

    //todo: since configs are validated client-side, do we need that?
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        return e.getMessage();
    }
}
