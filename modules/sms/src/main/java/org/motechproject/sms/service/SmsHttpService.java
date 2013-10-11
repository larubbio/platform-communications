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
import org.motechproject.sms.templates.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
        HttpMethod httpMethod = null;
        Integer failureCount = sms.getFailureCount();
        Integer httpStatus = null;
        String httpResponse = null;

        //todo: cleanup motechMessageId(ie: uuid) and SMS provider messageId
        if (!sms.hasMessageId()) {
            sms.setMessageId(java.util.UUID.randomUUID().toString().replace("-", ""));
        }

        Map<String, String> props = new HashMap<String, String>();
        props.put("recipients", template.recipientsAsString(sms.getRecipients()));
        props.put("message", sms.getMessage());
        props.put("uuid", sms.getMessageId());
        for (ConfigProp prop : config.getProps()) {
            props.put(prop.getName(), prop.getValue());
        }

        try {
            httpMethod = template.generateRequestFor(props);
            setAuthenticationInfo(template.getAuthentication());

            httpStatus = commonsHttpClient.executeMethod(httpMethod);
            httpResponse = httpMethod.getResponseBodyAsString();

            logger.info("HTTP status:{}, response:{}", httpStatus, httpResponse);
        }
        catch (Exception e) {
            logger.error("Error while communicating with '{}': {}", config.getName(), e);

            error = true;
        }
        finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }

        String msgForLog = sms.getMessage().replace("\n", "\\n");

        if (!error) {
            Response resp = template.getOutgoing().getResponse();
            if (httpStatus == 200) {
                //
                // analyze sms provider's response
                //
                if (resp.getMultiLineRecipientResponse()) {
                    if (resp.hasExtractSuccessRecipient()) {
                        List<String> failedRecipients = new ArrayList<String>();

                        //todo: store these in Template.Outgoing.Response class
                        Pattern pExtractSuccessRecipient = Pattern.compile(resp.getExtractSuccessRecipient());
                        Pattern pExtractSuccessMessageId = null;
                        if (resp.hasExtractSuccessMessageId()) {
                            pExtractSuccessMessageId = Pattern.compile(resp.getExtractSuccessMessageId());
                        }
                        Pattern pExtractFailureRecipient = null;
                        if (resp.hasExtractFailureRecipient()) {
                            pExtractFailureRecipient = Pattern.compile(resp.getExtractFailureRecipient());
                        }
                        Pattern pExtractFailureMessage = null;
                        if (resp.hasExtractFailureMessage()) {
                            pExtractFailureMessage = Pattern.compile(resp.getExtractFailureMessage());
                        }

                        for (String responseLine : httpResponse.split("\\r?\\n")) {
                            String recipient = resp.extractSuccessRecipient(responseLine);
                            if (recipient != null && !recipient.isEmpty()) {
                                String messageId = resp.extractSuccessMessageId(responseLine);
                                if (messageId != null) {
                                    logger.info(String.format("Successfully sent messageId %s '%s' to %s", messageId,
                                        msgForLog, recipient));
                                }
                                else {
                                    logger.info("Successfully sent message {} to {}", msgForLog, recipient);
                                }
                                //todo: post outbound success event
                            }
                            else {
                                recipient = resp.extractFailureRecipient(responseLine);
                                if (recipient != null && !recipient.isEmpty()) {
                                    String failureMessage = resp.extractFailureMessage(responseLine);
                                    if (failureMessage != null) {
                                        logger.info(String.format("Failed to sent message '%s' to %s: %s", msgForLog,
                                            recipient, failureMessage));
                                    }
                                    else {
                                        logger.error("Failed to send message {} to {}", msgForLog, recipient);
                                    }
                                    //todo #############################################################################
                                    //todo: add send event for this guy
                                    //todo #############################################################################
                                }
                                else {
                                    logger.error("Unknown error: {}", responseLine);
                                }
                            }
                        }
                    }
                    else {
                        throw new IllegalStateException(String.format(
                            "Error with '{}' template: multiLineRecipientResponse set, but no extractSuccessRecipient.",
                            template.getName()));
                    }
                }
                else if (resp.hasSuccessResponse()) {
                    //
                    // Simple one-size-fits-all success response from the provider
                    //
                    if (httpResponse.matches(resp.getSuccessResponse())) {
                        String messageForLog = sms.getMessage().replace("\n", "\\n");
                        String recipientsForLog = StringUtils.join(sms.getRecipients().iterator(), ",");
                        logger.info("SMS with message \"{}\" sent successfully to {}", messageForLog, recipientsForLog);
                        //todo addSmsRecord(recipients, message, sendTime, DELIVERY_CONFIRMED);
                        eventRelay.sendEventMessage(makeOutboundSmsSuccessEvent(sms.getConfig(), sms.getRecipients(),
                            sms.getMessage(), sms.getMessageId(), sms.getDeliveryTime(), failureCount));
                    }
                    else
                    {
                        error = true;
                    }
                } else {
                    //
                    // provider returned HTTP 200,  assume success
                    //
                }
            }
            else {
                error = true;
                logger.error("Delivery to SMS provider failed with HTTP {}: {}", httpStatus, httpResponse);
            }

            if (error) {
                failureCount++;
                if (failureCount < config.getMaxRetries()) {
                    //todo addSmsRecord(recipients, message, sendTime, KEEPTRYING);
                    logger.error("SMS delivery retry {} of {}", failureCount, config.getMaxRetries());
                    eventRelay.sendEventMessage(makeSendEvent(sms.getConfig(), sms.getRecipients(), sms.getMessage(),
                            sms.getMessageId(), sms.getDeliveryTime(), failureCount));
                }
                else {
                    logger.error("SMS delivery retry {} of {}, maximum reached, abandoning", failureCount,
                            config.getMaxRetries());
                    //todo addSmsRecord(recipients, message, sendTime, ABORTED);
                    eventRelay.sendEventMessage(makeOutboundSmsFailureEvent(sms.getConfig(), sms.getRecipients(),
                            sms.getMessage(), sms.getMessageId(), sms.getDeliveryTime(), failureCount));
                }
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
