package org.motechproject.sms.event;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SmsEvents {

    public static final String SEND_SMS = "send_sms";
    public static final String OUTBOUND_SMS_SUCCESS = "outbound_sms_success";
    public static final String OUTBOUND_SMS_FAILURE = "outbound_sms_failure";
    public static final String INBOUND_SMS = "inbound_sms";
    public static final String CONFIG = "config";
    public static final String RECIPIENTS = "recipients";
    public static final String RECIPIENT = "recipient";
    public static final String SENDER = "sender";
    public static final String TIMESTAMP = "timestamp";
    public static final String MESSAGE = "message";
    public static final String DELIVERY_TIME = "delivery_time";
    public static final String FAILURE_COUNT = "failure_count";
    public static final String MOTECH_ID = "motech_id";
    public static final String PROVIDER_ID = "provider_id";

    public static MotechEvent makeSendEvent(String config, List<String> recipients, String message, String motechId, String providerId) {
        return makeEvent(SEND_SMS, config, recipients, message, motechId, providerId, null, null);
    }

    public static MotechEvent makeSendEvent(String config, List<String> recipients, String message, String motechId, String providerId, DateTime deliveryTime) {
        return makeEvent(SEND_SMS, config, recipients, message, motechId, providerId, deliveryTime, null);
    }

    public static MotechEvent makeScheduledSendEvent(String config, List<String> recipients, String message, String motechId, String providerId) {
        MotechEvent event = makeEvent(SEND_SMS, config, recipients, message, motechId, providerId, null, null);
        //todo: use motechId instead of new uuid?
        //todo: randomUUID perf
        //MOTECH scheduler needs unique job ids
        event.getParameters().put(MotechSchedulerService.JOB_ID_KEY, UUID.randomUUID().toString());
        return event;
    }

    public static MotechEvent makeSendEvent(String config, List<String> recipients, String message, String motechId, String providerId, DateTime deliveryTime, Integer failureCount) {
        return makeEvent(SEND_SMS, config, recipients, message, motechId, providerId, deliveryTime, failureCount);
    }

    public static MotechEvent makeOutboundSmsFailureEvent(String config, List<String> recipients, String message, String motechId, String providerId, DateTime deliveryTime, Integer failureCount) {
        return makeEvent(OUTBOUND_SMS_FAILURE, config, recipients, message, motechId, providerId, deliveryTime, failureCount);
    }

    public static MotechEvent makeOutboundSmsSuccessEvent(String config, List<String> recipients, String message, String motechId, String providerId, DateTime deliveryTime, Integer failureCount) {
        return makeEvent(OUTBOUND_SMS_SUCCESS, config, recipients, message, motechId, providerId, deliveryTime, failureCount);
    }

    public static MotechEvent makeInboundSmsEvent(String config, String sender, String recipient, String message, String providerId, DateTime timestamp) {
        Map<String, Object> params = new HashMap<>();
        params.put(CONFIG, config);
        params.put(SENDER, sender);
        params.put(RECIPIENT, recipient);
        params.put(MESSAGE, message);
        params.put(PROVIDER_ID, providerId);
        params.put(TIMESTAMP, timestamp);
        return new MotechEvent(INBOUND_SMS, params);
    }

    private static MotechEvent makeEvent(String subject, String config, List<String> recipients, String message, String motechId, String providerId, DateTime deliveryTime, Integer failureCount) {
        Map<String, Object> params = new HashMap<>();
        params.put(CONFIG, config);
        params.put(RECIPIENTS, recipients);
        params.put(MESSAGE, message);
        params.put(MOTECH_ID, motechId);
        params.put(PROVIDER_ID, providerId);
        if (deliveryTime != null) {
            params.put(DELIVERY_TIME, deliveryTime);
        }
        if (failureCount != null) {
            params.put(FAILURE_COUNT, failureCount);
        }
        else {
            params.put(FAILURE_COUNT, 0);
        }
        return new MotechEvent(subject, params);
    }
}
