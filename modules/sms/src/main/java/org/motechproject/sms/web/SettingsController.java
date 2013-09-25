package org.motechproject.sms.web;

import org.motechproject.sms.model.Configs;
import org.motechproject.sms.model.SettingsDto;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@Controller
public class SettingsController {
    private SettingsFacade settingsFacade;
    private SettingsDto settingsDto;

    @Autowired
    public SettingsController(@Qualifier("smsSettings") final SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
        this.settingsDto = new SettingsDto(settingsFacade);
    }

    @RequestMapping(value = "/configs", method = RequestMethod.GET)
    @ResponseBody
    public Configs getConfigs() {
        return settingsDto.getConfigs();
    }

    @RequestMapping(value = "/templates", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Properties> getTemplates() {
        return settingsDto.getTemplates();
    }

/*
    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void setSettings(@RequestBody SettingsDto settings) {
        String host = settings.getHost();
        String port = settings.getPort();
        StringBuilder exceptionMessage = new StringBuilder();

        if (isBlank(host)) {
            exceptionMessage
                    .append(String.format(REQUIRED_FORMAT, MAIL_HOST_PROPERTY))
                    .append(NEW_LINE);
        }

        if (isBlank(port)) {
            exceptionMessage
                    .append(String.format(REQUIRED_FORMAT, MAIL_PORT_PROPERTY))
                    .append(NEW_LINE);
        } else if (!isNumeric(port)) {
            exceptionMessage
                    .append(String.format(NUMERIC_FORMAT, MAIL_PORT_PROPERTY))
                    .append(NEW_LINE);
        }

        if (exceptionMessage.length() > 0) {
            throw new IllegalStateException(exceptionMessage.toString());
        }

        settingsFacade.saveConfigProperties(SMS_PROPERTIES_FILE_NAME, settings.toProperties());
    }
*/

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        return e.getMessage();
    }
}
