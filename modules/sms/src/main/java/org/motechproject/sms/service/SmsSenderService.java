package org.motechproject.sms.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmsSenderService {

    private Logger logger = LoggerFactory.getLogger(SmsSenderService.class);
    private Settings settings;
    private Templates templates;
    private EventRelay eventRelay;
    private HttpClient commonsHttpClient;
    private MotechSchedulerService schedulerService;

    @Autowired
    public SmsSenderService(@Qualifier("smsSettings") SettingsFacade settingsFacade, EventRelay eventRelay,
                            HttpClient commonsHttpClient, MotechSchedulerService schedulerService) {
        settings = new Settings(settingsFacade);
        templates = new Templates(settingsFacade);
        this.eventRelay = eventRelay;
        this.commonsHttpClient = commonsHttpClient;
        this.schedulerService = schedulerService;
    }

    public void send(OutgoingSms sms) {

        logger.info("Business happening here, using {}", sms.toString());

        try {
            //todo: verify we're not reading settings from file/db every time
            ConfigsDto configsDto = settings.getConfigsDto();
            Config config = configsDto.getConfig(sms.getConfig());
            Template template = templates.getTemplate(config.getTemplateName());
            HttpMethod httpMethod;
            Map<String, String> replaceMap = new HashMap<String, String>();

            //todo: do we want a template-variable recipient separator?
            replaceMap.put("recipients", StringUtils.join(sms.getRecipients(), ","));
            replaceMap.put("message", sms.getMessage());

            if (template.getHttpMethod() == HttpMethodType.GET) {
                logger.info("Creating GET request");
                PostMethod postMethod = new PostMethod(template.getURL());
                postMethod.setRequestHeader("Content-Type", PostMethod.FORM_URL_ENCODED_CONTENT_TYPE);

                Map<String, String> bodyParameters = stringToMap(template.getBodyParameters());
                for (Map.Entry<String, String> entry: bodyParameters.entrySet()) {
                    String value;
                    if (replaceMap.containsKey(entry.getKey())) {
                        value = replaceMap.get(entry.getKey());
                    }
                    else {
                        value = entry.getValue();
                    }
                    postMethod.setParameter(entry.getKey(), value);
                }

                httpMethod = postMethod;
            }
            else {
                //POST
                logger.info("Creating POST request");
                httpMethod = new GetMethod(template.getURL());
            }

            List<NameValuePair> queryStringValues = new ArrayList<NameValuePair>();
            Map<String, String> queryParameters = stringToMap(template.getQueryParameters());
            for (Map.Entry< String, String > entry : queryParameters.entrySet()) {
                String value;
                if (replaceMap.containsKey(entry.getKey())) {
                    value = replaceMap.get(entry.getKey());
                }
                else {
                    value = entry.getValue();
                }
                queryStringValues.add(new NameValuePair(entry.getKey(), value));
            }
            NameValuePair[] nvpa = queryStringValues.toArray(new NameValuePair[queryStringValues.size()]);
            httpMethod.setQueryString(nvpa);


            if (template.hasAuthentication()) {
                commonsHttpClient.getParams().setAuthenticationPreemptive(true);
                commonsHttpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(template.getUsername(), template.getPassword()));
            }

            int status = commonsHttpClient.executeMethod(httpMethod);
            String response = httpMethod.getResponseBodyAsString();

            logger.info("HTTP Status:" + status + "|Response:" + response);

            //todo: post new event if we failed, unless we reached maxRetry
        } catch (Exception e) {
            logger.error("SMSDeliveryFailure due to : ", e);

            if (failureCount >= maxRetries) {
                addSmsRecord(recipients, message, sendTime, ABORTED);
            } else {
                addSmsRecord(recipients, message, sendTime, KEEPTRYING);
            }

            raiseFailureEvent(recipients, message, failureCount);

            return;
        } finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }

        if (!new SMSGatewayResponse(template(), response).isSuccess()) {
            log.error(String.format("SMS delivery failed. Retrying...; Response: %s", response));
            addSmsRecord(recipients, message, sendTime, KEEPTRYING);
            raiseFailureEvent(recipients, message, failureCount);
        } else {
            try {
                log.debug("SMS with message : {}, sent successfully to {}", message,
                        StringUtils.join(recipients.iterator(), ","));
                addSmsRecord(recipients, message, sendTime, DELIVERY_CONFIRMED);
            } catch (Exception e) {
                log.error("SMS record failure due to : ", e);
            }
        }
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
                String[] keyval = pair.split("=");
                ret.put(keyval[0], keyval[1]);
            }
        }
        return ret;
    }
}
