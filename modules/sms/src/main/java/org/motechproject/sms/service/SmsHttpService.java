package org.motechproject.sms.service;

import org.apache.http.client.HttpClient;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.model.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

//@Service
public class SmsHttpService {
    private static Logger log = LoggerFactory.getLogger(SmsHttpService.class);
    private EventRelay eventRelay;
    private HttpClient commonsHttpClient;
    private MotechSchedulerService schedulerService;
    private Settings settings;

    @Autowired
    public SmsHttpService(EventRelay eventRelay, HttpClient commonsHttpClient, MotechSchedulerService schedulerService,
                          @Qualifier("smsSettings") SettingsFacade settings) {
        this.eventRelay = eventRelay;
        this.commonsHttpClient = commonsHttpClient;
        this.schedulerService = schedulerService;
        this.settings = new Settings(settings);
    }

/*
    public void sendSms(List<String> recipients, String message) throws SmsDeliveryFailureException {
        sendSms(recipients, message, 0);
    }
    public void sendSms(List<String> recipients, String message, Integer failureCount) throws SmsDeliveryFailureException {
        if (CollectionUtils.isEmpty(recipients) || StringUtils.isEmpty(message)) {
            throw new IllegalArgumentException("Recipients or Message should not be empty");
        }

        String response = null;
        HttpMethod httpMethod = null;
        DateTime sendTime = DateUtil.now();

        try {
            httpMethod = smsHttpTemplate.generateRequestFor(recipients, message);
            setAuthenticationInfo(smsHttpTemplate.getAuthentication());

            int status = commonsHttpClient.executeMethod(httpMethod);
            response = httpMethod.getResponseBodyAsString();

            log.info("HTTP Status:" + status + "|Response:" + response);
        } catch (Exception e) {
            log.error("SMSDeliveryFailure due to : ", e);

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

    public void sendSms(List<String> recipients, String message, DateTime deliveryTime) throws SmsDeliveryFailureException {
        sendSms(recipients, message, 0, deliveryTime);
    }

    public void sendSms(List<String> recipients, String message, Integer failureCount, DateTime deliveryTime) throws SmsDeliveryFailureException {
        RunOnceSchedulableJob schedulableJob = new RunOnceSchedulableJob(new SendSmsDTEvent(recipients, message, failureCount).toMotechEvent(), deliveryTime.toDate());
        schedulerService.safeScheduleRunOnceJob(schedulableJob);

        log.info(String.format("Scheduling message [%s] to number %s at %s.", message, recipients, deliveryTime.toString()));
    }

    private void setAuthenticationInfo(Authentication authentication) {
        if (authentication == null) {
            return;
        }

        commonsHttpClient.getParams().setAuthenticationPreemptive(true);
        commonsHttpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(authentication.getUsername(), authentication.getPassword()));
    }

    //Recreating the template for every request so that latest templates can be changed
    private SmsHttpTemplate template() {
        return templateReader.getTemplate();
    }

    private void raiseFailureEvent(List<String> recipients, String message, int failureCount) {
        for (String recipient : recipients) {
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(RECIPIENT, recipient);
            parameters.put(MESSAGE, message);
            parameters.put(FAILURE_COUNT, failureCount + 1);
            eventRelay.sendEventMessage(new MotechEvent(SMS_FAILURE_NOTIFICATION, parameters));
        }
    }

    private void addSmsRecord(List<String> recipients, String message, DateTime sendTime, DeliveryStatus deliveryStatus) {
        for (String recipient : recipients) {
            smsAuditService.log(new SmsRecord(
                    OUTBOUND, recipient, message, sendTime, deliveryStatus,
                    Integer.toString(Math.abs(random.nextInt()))
            ));
        }
    }
*/
}
