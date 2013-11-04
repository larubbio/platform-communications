package org.motechproject.sms.event;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for dealing with events
 */
public class SmsEvents {

    public static final String SEND_SMS = "send_sms";
    public static final String OUTBOUND_SMS_RETRYING = "outbound_sms_retrying";
    public static final String OUTBOUND_SMS_ABORTED = "outbound_sms_aborted";
    public static final String OUTBOUND_SMS_SCHEDULED = "outbound_sms_scheduled";
    public static final String OUTBOUND_SMS_PENDING = "outbound_sms_pending";
    public static final String OUTBOUND_SMS_DISPATCHED = "outbound_sms_dispatched";
    public static final String OUTBOUND_SMS_DELIVERY_CONFIRMED = "outbound_sms_delivery_confirmed";
    public static final String OUTBOUND_SMS_FAILURE_CONFIRMED = "outbound_sms_failure_confirmed";
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
    public static final String PROVIDER_STATUS = "provider_status";

    public static MotechEvent makeSendEvent(String config, List<String> recipients, String message, String motechId,
                                            String providerId) {
        return makeEvent(SEND_SMS, config, recipients, message, motechId, providerId, null, null, null);
    }

    public static MotechEvent makeSendEvent(String config, List<String> recipients, String message, String motechId,
                                            String providerId, DateTime deliveryTime) {
        return makeEvent(SEND_SMS, config, recipients, message, motechId, providerId, deliveryTime, null, null);
    }

    public static MotechEvent makeScheduledSendEvent(String config, List<String> recipients, String message,
                                                     String motechId, String providerId) {
        MotechEvent event = makeEvent(SEND_SMS, config, recipients, message, motechId, providerId, null, null, null);
        //MOTECH scheduler needs unique job ids, so adding motechId as job_id_key will do that
        event.getParameters().put(MotechSchedulerService.JOB_ID_KEY, motechId);
        return event;
    }

    public static MotechEvent makeSendEvent(String config, List<String> recipients, String message, String motechId,
                                            String providerId, DateTime deliveryTime, Integer failureCount) {
        return makeEvent(SEND_SMS, config, recipients, message, motechId, providerId, deliveryTime, failureCount, null);
    }

    public static MotechEvent makeOutboundSmsEvent(String subject, String config, List<String> recipients,
                                                   String message, String motechId, String providerId,
                                                   DateTime deliveryTime, Integer failureCount) {
        return makeEvent(subject, config, recipients, message, motechId, providerId, deliveryTime,
                failureCount, null);
    }

    public static MotechEvent makeOutboundSmsEvent(String subject, String config, List<String> recipients,
                                                   String message, String motechId, String providerId,
                                                   DateTime deliveryTime, Integer failureCount, String providerStatus) {
        return makeEvent(subject, config, recipients, message, motechId, providerId, deliveryTime,
                failureCount, providerStatus);
    }

    public static MotechEvent makeInboundSmsEvent(String config, String sender, String recipient, String message,
                                                  String providerId, DateTime timestamp) {
        Map<String, Object> params = new HashMap<>();
        params.put(CONFIG, config);
        params.put(SENDER, sender);
        params.put(RECIPIENT, recipient);
        params.put(MESSAGE, message);
        params.put(PROVIDER_ID, providerId);
        params.put(TIMESTAMP, timestamp);
        return new MotechEvent(INBOUND_SMS, params);
    }

    private static MotechEvent makeEvent(String subject, String config, List<String> recipients, String message,
                                         String motechId, String providerId, DateTime deliveryTime,
                                         Integer failureCount, String providerStatus) {
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
        if (providerStatus != null) {
            params.put(PROVIDER_STATUS, providerStatus);
        }
        return new MotechEvent(subject, params);
    }
}
