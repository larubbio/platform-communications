package org.motechproject.sms.web;

import org.apache.http.params.HttpParams;
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
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;

import static org.motechproject.sms.event.SmsEvents.makeInboundSmsEvent;
import static org.motechproject.sms.event.SmsEvents.makeOutboundSmsSuccessEvent;

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
    public String handleIncoming(@PathVariable String configName, WebRequest request) {
        String sender = null;
        String recipient = null;
        String message = null;
        String messageId = null;
        DateTime timestamp = null;
        String response = "OK"; //todo: better default?

        logger.info("Received SMS, configName = {}, params = {}", configName, request.getParameterMap().toString());

        Config config;
        if (configsDto.hasConfig(configName)) {
            config = configsDto.getConfig(configName);
        }
        else {
            //todo: do we really want to do that?
            config = configsDto.getDefaultConfig();
        }
        Template template = templates.getTemplate(config.getTemplateName());
        Map<String, String[]> params = request.getParameterMap();

        if (params.containsKey(template.getIncoming().getSenderKey())) {
            //todo: see why param values are string arrays & make sure we're doing the right stuff
            sender = params.get(template.getIncoming().getSenderKey())[0];
        }

        if (params.containsKey(template.getIncoming().getRecipientKey())) {
            recipient = params.get(template.getIncoming().getRecipientKey())[0];
        }

        if (params.containsKey(template.getIncoming().getMessageKey())) {
            message = params.get(template.getIncoming().getMessageKey())[0];
        }

        if (params.containsKey(template.getIncoming().getMsgIdKey())) {
            messageId = params.get(template.getIncoming().getMsgIdKey())[0];
        }

        if (params.containsKey(template.getIncoming().getTimestampKey())) {
            timestamp = DateTime.parse(params.get(template.getIncoming().getTimestampKey())[0]);
        }

        if (template.getIncoming().getResponse() != null && template.getIncoming().getResponse().length() > 0) {
            response = template.getIncoming().getResponse();
        }

        eventRelay.sendEventMessage(makeInboundSmsEvent(config.getName(), sender, recipient, message, messageId,
                timestamp));

        return response;
    }
}
