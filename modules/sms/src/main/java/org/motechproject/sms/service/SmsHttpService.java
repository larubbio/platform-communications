package org.motechproject.sms.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.lang.StringUtils;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.event.SendSmsEvent;
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

        Map<String, String> props = new HashMap<String, String>();
        props.put("recipients", template.recipientsAsString(sms.getRecipients()));
        props.put("message", sms.getMessage());
        for (ConfigProp prop : config.getProps()) {
            props.put(prop.getName(), prop.getValue());
        }

        try {
            httpMethod = template.generateRequestFor(props);
            setAuthenticationInfo(template.getAuthentication());

            int status = commonsHttpClient.executeMethod(httpMethod);
            response = httpMethod.getResponseBodyAsString();

            logger.info("HTTP Status:" + status + "|Response:" + response);
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
            if (response != null && response.matches(template.getSuccessfulResponsePattern())) {
                logger.debug("SMS with message : {}, sent successfully to {}", sms.getMessage(), StringUtils.join(sms.getRecipients().iterator(), ","));
                //todo addSmsRecord(recipients, message, sendTime, DELIVERY_CONFIRMED);
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
                logger.error("SMS delivery failure {} of {}, will keep trying", failureCount, config.getMaxRetries());
                eventRelay.sendEventMessage(new SendSmsEvent(sms.getConfig(), sms.getRecipients(), sms.getMessage(),
                        failureCount).toMotechEvent());
            }
            else {
                logger.error("SMS delivery failure {} of {}, maximum reached, abandoning", failureCount,
                        config.getMaxRetries());
                //todo addSmsRecord(recipients, message, sendTime, ABORTED);
                //todo? eventRelay.sendEventMessage(new MotechEvent(SMS_FAILURE_NOTIFICATION, parameters));
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

    static private void replaceValues(Map<String, String> replaceMap, Map<String, String> replaceValues) {
        for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
            if (replaceValues.containsKey(entry.getKey())) {
                replaceValues.put(entry.getKey(), entry.getValue());
            }
        }
    }

    //from http://stackoverflow.com/questions/10514473/string-to-hashmap-java
    static private Map<String, String> stringToMap(String in) {
        Map<String, String> ret = new HashMap<String, String>();
        if (in != null && !in.isEmpty()) {
            String[] pairs = in.split("&");
            for (String pair : pairs) {
                String[] keyval = pair.split(":");
                ret.put(keyval[0], keyval[1]);
            }
        }
        return ret;
    }
}
