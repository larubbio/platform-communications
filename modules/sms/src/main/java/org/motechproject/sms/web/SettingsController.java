package org.motechproject.sms.web;

import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.model.SettingsDto;
import org.motechproject.sms.model.Settings;
import org.motechproject.sms.model.Templates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Templates templates;
    private static final Logger logger = LoggerFactory.getLogger(Settings.class);

    @Autowired
    public SettingsController(@Qualifier("smsSettings") final SettingsFacade settingsFacade) {
        logger.info("Initializing SettingsController");
        this.settingsFacade = settingsFacade;
        this.templates = new Templates(settingsFacade);
        this.settings = new Settings(settingsFacade);
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    @ResponseBody
    public SettingsDto getConfigs() {
        return settings.getSettingsDto();
    }

    @RequestMapping(value = "/templates", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Properties> getTemplates() {
        return templates.getTemplates();
    }

    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void setConfigs(@RequestBody SettingsDto settingsDto) {

        settingsDto.clearErrors();

        //todo: validate settingsDto & throw IllegalStateException if invalid ?

        settings.setConfigs(settingsFacade, settingsDto);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {

        logger.debug("SettingsController Exception:" + e);
        return e.getMessage();
    }
}
