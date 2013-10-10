package org.motechproject.sms.web;

import org.joda.time.DateTime;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.settings.Config;
import org.motechproject.sms.settings.ConfigsDto;
import org.motechproject.sms.settings.Settings;
import org.motechproject.sms.templates.Template;
import org.motechproject.sms.templates.TemplateReader;
import org.motechproject.sms.templates.Templates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.motechproject.sms.event.SmsEvents.makeInboundSmsEvent;

/**
 * todo
 */
@Controller
@RequestMapping(value = "/incoming")
public class IncomingController {

    private Logger logger = LoggerFactory.getLogger(IncomingController.class);
    private Settings settings;
    private ConfigsDto configsDto;
    private Templates templates;
    private EventRelay eventRelay;

    @Autowired
    public IncomingController(@Qualifier("smsSettings") SettingsFacade settingsFacade, EventRelay eventRelay,
                              TemplateReader templateReader) {
        this.eventRelay = eventRelay;
        settings = new Settings(settingsFacade);
        configsDto = settings.getConfigsDto();
        templates = templateReader.getTemplates();
    }


    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/{configName}", method = RequestMethod.GET)
    public String handleIncoming(@PathVariable String configName, @RequestParam Map<String, String> params) {
        String sender = null;
        String recipient = null;
        String message = null;
        String messageId = null;
        DateTime timestamp = null;
        String response = "OK"; //todo: better default?

        logger.info("Received SMS, configName = {}, params = {}", configName, params);

        Config config;
        if (configsDto.hasConfig(configName)) {
            config = configsDto.getConfig(configName);
        }
        else {
            //todo: do we really want to do that?
            config = configsDto.getDefaultConfig();
        }
        Template template = templates.getTemplate(config.getTemplateName());

        if (params.containsKey(template.getIncoming().getSenderKey())) {
            sender = params.get(template.getIncoming().getSenderKey());
        }

        if (params.containsKey(template.getIncoming().getRecipientKey())) {
            recipient = params.get(template.getIncoming().getRecipientKey());
        }

        if (params.containsKey(template.getIncoming().getMessageKey())) {
            message = params.get(template.getIncoming().getMessageKey());
        }

        if (params.containsKey(template.getIncoming().getMsgIdKey())) {
            messageId = params.get(template.getIncoming().getMsgIdKey());
        }

        if (params.containsKey(template.getIncoming().getTimestampKey())) {
            timestamp = DateTime.parse(params.get(template.getIncoming().getTimestampKey()));
        }

        if (template.getIncoming().getResponse() != null && template.getIncoming().getResponse().length() > 0) {
            response = template.getIncoming().getResponse();
        }

        eventRelay.sendEventMessage(makeInboundSmsEvent(config.getName(), sender, recipient, message, messageId,
                timestamp));

        return response;
    }
}
