package org.motechproject.sms.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.lang.StringUtils;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.settings.*;
import org.motechproject.sms.templates.Authentication;
import org.motechproject.sms.templates.Template;
import org.motechproject.sms.templates.TemplateReader;
import org.motechproject.sms.templates.Templates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.sms.event.SmsEvents.*;

@Service
public class SmsHttpService {

    private Logger logger = LoggerFactory.getLogger(SmsHttpService.class);
    private Settings settings;
    private ConfigsDto configsDto;
    private Templates templates;
    private EventRelay eventRelay;
    private HttpClient commonsHttpClient;
    private MotechSchedulerService schedulerService;

    @Autowired
    public SmsHttpService(@Qualifier("smsSettings") SettingsFacade settingsFacade, EventRelay eventRelay,
                          HttpClient commonsHttpClient, MotechSchedulerService schedulerService,
                          TemplateReader templateReader) {

        //todo: unified module-wide caching strategy
        settings = new Settings(settingsFacade);
        configsDto = settings.getConfigsDto();
        templates = templateReader.getTemplates();
        this.eventRelay = eventRelay;
        this.commonsHttpClient = commonsHttpClient;
        this.schedulerService = schedulerService;
    }


    public void send(OutgoingSms sms) {
        Boolean error = false;
        Config config = configsDto.getConfigOrDefault(sms.getConfig());
        Template template = templates.getTemplate(config.getTemplateName());
        String response = null;
        HttpMethod httpMethod = null;
        Integer failureCount = sms.getFailureCount();
        Integer httpStatus = null;
        String httpResponse = null;

        Map<String, String> props = new HashMap<String, String>();
        props.put("recipients", template.recipientsAsString(sms.getRecipients()));
        props.put("message", sms.getMessage());
        for (ConfigProp prop : config.getProps()) {
            props.put(prop.getName(), prop.getValue());
        }

        try {
            httpMethod = template.generateRequestFor(props);
            setAuthenticationInfo(template.getAuthentication());

            httpStatus = commonsHttpClient.executeMethod(httpMethod);
            httpResponse = httpMethod.getResponseBodyAsString();

            logger.info("HTTP status:" + httpStatus + ", response:" + httpResponse);
        }
        catch (Exception e) {
            logger.error("SMSDeliveryFailure due to : ", e);

            error = true;
        }
        finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }

        if (!error) {
            if (template.outgoingSuccess(httpStatus, httpResponse)) {
                logger.debug("SMS with message \"{}\" sent successfully to {}", sms.getMessage(), StringUtils.join(sms.getRecipients().iterator(), ","));
                //todo addSmsRecord(recipients, message, sendTime, DELIVERY_CONFIRMED);
                eventRelay.sendEventMessage(makeOutboundSmsSuccessEvent(sms.getConfig(), sms.getRecipients(),
                        sms.getMessage(), sms.getDeliveryTime(), failureCount));
            }
            else {
                error = true;
                logger.error(String.format("SMS delivery failed. Retrying...; Response: %s", response));
                //todo addSmsRecord(recipients, message, sendTime, KEEPTRYING);
                //todo raiseFailureEvent(recipients, message, failureCount);
            }
        }

        if (error) {
            failureCount++;
            if (failureCount < config.getMaxRetries()) {
                //todo addSmsRecord(recipients, message, sendTime, KEEPTRYING);
                logger.error("SMS delivery retry {} of {}", failureCount, config.getMaxRetries());
                eventRelay.sendEventMessage(makeSendEvent(sms.getConfig(), sms.getRecipients(), sms.getMessage(),
                        sms.getDeliveryTime(), failureCount));
            }
            else {
                logger.error("SMS delivery retry {} of {}, maximum reached, abandoning", failureCount,
                        config.getMaxRetries());
                //todo addSmsRecord(recipients, message, sendTime, ABORTED);
                eventRelay.sendEventMessage(makeOutboundSmsFailureEvent(sms.getConfig(), sms.getRecipients(),
                        sms.getMessage(), sms.getDeliveryTime(), failureCount));
            }
        }
    }

    private void setAuthenticationInfo(Authentication authentication) {
        if (authentication == null) {
            return;
        }

        commonsHttpClient.getParams().setAuthenticationPreemptive(true);
        commonsHttpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(authentication.getUsername(), authentication.getPassword()));
    }
}
