package org.motechproject.sms.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.sms.audit.SmsRecord;
import org.motechproject.sms.configs.Config;
import org.motechproject.sms.configs.ConfigProp;
import org.motechproject.sms.configs.ConfigReader;
import org.motechproject.sms.configs.Configs;
import org.motechproject.sms.service.OutgoingSms;
import org.motechproject.sms.service.SmsAuditService;
import org.motechproject.sms.templates.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.sms.audit.SmsDeliveryStatus.*;
import static org.motechproject.sms.audit.SmsType.OUTBOUND;
import static org.motechproject.sms.event.SmsEvents.*;

@Service
public class SmsHttpService {

    private Logger logger = LoggerFactory.getLogger(SmsHttpService.class);
    private ConfigReader configReader;
    private Configs configs;
    private Templates templates;
    private EventRelay eventRelay;
    private HttpClient commonsHttpClient;
    private MotechSchedulerService schedulerService;
    private SmsAuditService smsAuditService;
    @Autowired
    private PlatformSettingsService settingsService;

    //todo: which @Autowired to use?

    @Autowired
    public SmsHttpService(@Qualifier("smsSettings") SettingsFacade settingsFacade, EventRelay eventRelay,
                          HttpClient commonsHttpClient, MotechSchedulerService schedulerService,
                          TemplateReader templateReader, SmsAuditService smsAuditService) {

        //todo: unified module-wide caching & refreshing strategy
        configReader = new ConfigReader(settingsFacade);
        configs = configReader.getConfigs();
        templates = templateReader.getTemplates();
        this.eventRelay = eventRelay;
        this.commonsHttpClient = commonsHttpClient;
        this.schedulerService = schedulerService;
        this.smsAuditService = smsAuditService;
    }

    public synchronized void send(OutgoingSms sms) {
        Boolean error = false;
        Config config = configs.getConfigOrDefault(sms.getConfig());
        Template template = templates.getTemplate(config.getTemplateName());
        HttpMethod httpMethod = null;
        Integer failureCount = sms.getFailureCount();
        Integer httpStatus = null;
        String httpResponse = null;
        List<String> failedRecipients = new ArrayList<String>();
        Boolean providerResponseParsingError = false;
        Map<String, String> errorMessages = new HashMap<String, String>();

        Map<String, String> props = new HashMap<String, String>();
        props.put("recipients", template.recipientsAsString(sms.getRecipients()));
        props.put("message", sms.getMessage());
        props.put("motechId", sms.getMotechId());
        props.put("callback", settingsService.getPlatformSettings().getServerUrl() + "/module/sms/status/" +
                config.getName());

        for (ConfigProp prop : config.getProps()) {
            props.put(prop.getName(), prop.getValue());
        }

        try {
            httpMethod = template.generateRequestFor(props);
            if (template.getOutgoing().getHasAuthentication()) {
                //todo: check if we have a user/pass and log error if not?
                String u = props.get("username");
                String p = props.get("password");
                commonsHttpClient.getParams().setAuthenticationPreemptive(true);
                commonsHttpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(u, p));
            }

            httpStatus = commonsHttpClient.executeMethod(httpMethod);
            httpResponse = httpMethod.getResponseBodyAsString();

            logger.debug("HTTP status:{}, response:{}", httpStatus, httpResponse.replace("\n", "\\n"));

            //todo: serialize access to configs, ie: one provider may allow 100 sms/min and another may allow 10...
            //This prevents us from sending more messages per second than the provider allows
            Integer milliseconds = template.getOutgoing().getMillisecondsBetweenMessages();
            logger.debug("Sleeping {}ms", milliseconds);
            try {
                Thread.sleep(milliseconds);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            logger.debug("Thread id {}", Thread.currentThread().getId());
        }
        catch (Exception e) {
            String errorMessage = String.format("Error while communicating with '%s': %s", config.getName(), e);
            logger.error(errorMessage);
            //todo audit log?
            //todo something like below
            errorMessages.put("all", errorMessage);

            error = true;
        }
        finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }

        String msgForLog = sms.getMessage().replace("\n", "\\n");
        Response resp = template.getOutgoing().getResponse();

        if (!error) {
            if ((resp.hasSuccessStatus() && (resp.checkSuccessStatus(httpStatus))) || httpStatus == 200) {
                //
                // analyze sms provider's response
                //
                if (resp.supportsMultiLineRecipientResponse()) {
                    for (String responseLine : httpResponse.split("\\r?\\n")) {
                        // todo: as of now, assume all providers return one msgid & recipient per line
                        // todo: but if we discover a provider that doesn't, then we'll add code here...

                        // Some multi-line response providers have a special case for single recipients
                        if (sms.getRecipients().size() == 1 && resp.supportsSingleRecipientResponse()) {
                            String messageId = resp.extractSingleSuccessMessageId(responseLine);
                            if (messageId != null) {
                                //
                                // success
                                //
                                logger.info(String.format("Successfully sent messageId %s '%s' to %s",
                                        messageId, msgForLog, sms.getRecipients().get(0)));
                                smsAuditService.log(new SmsRecord(config.getName(), OUTBOUND,
                                    sms.getRecipients().get(0), sms.getMessage(), now(), DISPATCHED, sms.getMotechId(),
                                    messageId, null));
                                //todo: post outbound success event
                            }
                            else {
                                //
                                // failure
                                //
                                error = true;
                                String failureMessage = resp.extractSingleFailureMessage(responseLine);
                                logger.error(String.format("Failed to sent message '%s' to %s: %s", msgForLog,
                                        sms.getRecipients().get(0), failureMessage));
                                errorMessages.put(sms.getRecipients().get(0), failureMessage);
                                failedRecipients.add(sms.getRecipients().get(0));
                                //todo: post outbound failure event
                            }
                        }
                        else {
                            String[] messageAndRecipient = resp.extractSuccessMessageIdAndRecipient(responseLine);

                            if (messageAndRecipient != null) {
                                //
                                // success
                                //
                                logger.info(String.format("Successfully sent messageId %s '%s' to %s",
                                    messageAndRecipient[0], msgForLog, messageAndRecipient[1]));
                                smsAuditService.log(new SmsRecord(config.getName(), OUTBOUND, messageAndRecipient[1],
                                    sms.getMessage(), now(), DISPATCHED, sms.getMotechId(), messageAndRecipient[0],
                                    null));
                                    //todo: post outbound success event
                            }
                            else {
                                //
                                // failure
                                //
                                error = true;
                                messageAndRecipient = resp.extractFailureMessageAndRecipient(responseLine);
                                if (messageAndRecipient == null) {
                                    providerResponseParsingError = true;
                                    logger.error(String.format(
                                        "Failed to sent message '%s', likely config or template error: unable to parse provider's response: %s",
                                        msgForLog, responseLine));
                                    //todo: do we really want to log that or is the tomcat log (above) sufficient??
                                    errorMessages.put("all", responseLine);
                                }
                                else {
                                    logger.error(String.format("Failed to sent message '%s' to %s: %s", msgForLog,
                                        messageAndRecipient[1], messageAndRecipient[0]));
                                    failedRecipients.add(messageAndRecipient[1]);
                                    errorMessages.put(messageAndRecipient[1], messageAndRecipient[0]);
                                }
                                //todo: post outbound failure event?
                            }
                        }
                    }
                }
                else if (resp.hasSuccessResponse() && !resp.checkSuccessResponse(httpResponse)) {
                    error = true;

                    String failureMessage = resp.extractSingleFailureMessage(httpResponse);
                    if (failureMessage != null) {
                        logger.error(String.format("Failed to sent message '%s' to %s: %s", msgForLog,
                                sms.getRecipients().get(0), failureMessage));
                    }
                    else {
                        logger.error(String.format("Failed to sent message '%s' to %s: %s", msgForLog,
                                sms.getRecipients().get(0), httpResponse));
                    }
                    //todo audit ?
                }
                else {
                    //
                    // Either straight HTTP 200, or matched successful response
                    //
                    String providerId = resp.extractSingleSuccessMessageId(httpResponse);

                    logger.info("SMS with message \"{}\" sent successfully to {}", msgForLog,
                        template.recipientsAsString(sms.getRecipients()));
                    eventRelay.sendEventMessage(makeOutboundSmsSuccessEvent(sms.getConfig(), sms.getRecipients(),
                        sms.getMessage(), sms.getMotechId(), providerId, sms.getDeliveryTime(), failureCount));
                    smsAuditService.log(new SmsRecord(config.getName(), OUTBOUND, sms.getRecipients().get(0),
                            sms.getMessage(), now(), DISPATCHED, sms.getMotechId(), providerId, null));
                }
            }
            else {
                error = true;
                String key = sms.getRecipients().size() == 1 ? sms.getRecipients().get(0) : "all";

                String failureMessage = resp.extractGeneralFailureMessage(httpResponse);
                if (failureMessage != null) {
                    logger.error("Delivery to SMS provider failed with HTTP {}: {}", httpStatus, failureMessage);
                    errorMessages.put(key, failureMessage);
                }
                else {
                    logger.error("Delivery to SMS provider failed with HTTP {}: {}", httpStatus, httpResponse);
                    errorMessages.put(key, httpResponse);
                }
            }
        }

        if (error) {
            failureCount++;
            List<String> recipients;

            // todo: do we want to add UNKNOWN status log events if we have a provider response parsing error?
            // Best to assume failure or unknown for all recipients if we can't parse provider's response
            // But the trade off is we might send an sms more than once.
            // todo: check we're happy with that
            //
            //                                            ********************************
            //                                            vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
            if (resp.supportsMultiLineRecipientResponse() && !providerResponseParsingError) {
                recipients = failedRecipients;
            }
            else {
                recipients = sms.getRecipients();
            }

            if (failureCount < config.getMaxRetries()) {
                logger.error("SMS delivery retry {} of {}", failureCount, config.getMaxRetries());
                eventRelay.sendEventMessage(makeSendEvent(sms.getConfig(), recipients, sms.getMessage(),
                        sms.getMotechId(), null, sms.getDeliveryTime(), failureCount));
                if (errorMessages.containsKey("all")) {
                    smsAuditService.log(new SmsRecord(config.getName(), OUTBOUND, recipients.toString(),
                            sms.getMessage(), now(), RETRYING, sms.getMotechId(), null, errorMessages.get("all")));
                }
                else {
                    for (String recipient : recipients) {
                        smsAuditService.log(new SmsRecord(config.getName(), OUTBOUND, recipient, sms.getMessage(),
                                now(), RETRYING, sms.getMotechId(), null, errorMessages.get(recipient)));
                    }
                }
            }
            else {
                logger.error("SMS delivery retry {} of {}, maximum reached, abandoning", failureCount,
                        config.getMaxRetries());
                eventRelay.sendEventMessage(makeOutboundSmsFailureEvent(sms.getConfig(), recipients,
                        sms.getMessage(), sms.getMotechId(), null, sms.getDeliveryTime(), failureCount));
                if (errorMessages.containsKey("all")) {
                    smsAuditService.log(new SmsRecord(config.getName(), OUTBOUND, recipients.toString(),
                            sms.getMessage(), now(), ABORTED, sms.getMotechId(), null, errorMessages.get("all")));
                }
                else {
                    for (String recipient : recipients) {
                        smsAuditService.log(new SmsRecord(config.getName(), OUTBOUND, recipient, sms.getMessage(),
                                now(), ABORTED, sms.getMotechId(), null, errorMessages.get(recipient)));
                    }
                }
            }
        }
    }
}
