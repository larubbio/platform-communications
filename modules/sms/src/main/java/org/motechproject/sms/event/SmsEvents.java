package org.motechproject.sms.event;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.commons.date.util.DateUtil.now;


/**
 * MotechEvent Helper class
 */
public class SmsEvents {

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
    public static final String DELIVERY_TIME = "delivery_time";
    public static final String TIMESTAMP = "timestamp";
    public static final String MESSAGE = "message";
    public static final String FAILURE_COUNT = "failure_count";
    public static final String MOTECH_ID = "motech_id";
    public static final String PROVIDER_MESSAGE_ID = "provider_message_id";
    public static final String PROVIDER_STATUS = "provider_status";

    public static MotechEvent inboundEvent(String config, String sender, String recipient, String message,
                                           String providerMessageId, DateTime timestamp) {
        Map<String, Object> params = new HashMap<>();
        params.put(CONFIG, config);
        params.put(SENDER, sender);
        params.put(RECIPIENT, recipient);
        params.put(MESSAGE, message);
        params.put(PROVIDER_MESSAGE_ID, providerMessageId);
        params.put(TIMESTAMP, timestamp);
        return new MotechEvent(INBOUND_SMS, params);
    }

    public static MotechEvent outboundEvent(String subject, String config, List<String> recipients, String message,
                                            String motechId, String providerMessageId, Integer failureCount,
                                            String providerStatus, DateTime timestamp) {
        Map<String, Object> params = new HashMap<>();
        params.put(CONFIG, config);
        params.put(RECIPIENTS, recipients);
        params.put(MESSAGE, message);
        params.put(MOTECH_ID, motechId);
        if (providerMessageId != null) {
            params.put(PROVIDER_MESSAGE_ID, providerMessageId);
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
        if (timestamp == null) {
            params.put(TIMESTAMP, now());
        }
        else {
            params.put(TIMESTAMP, timestamp);
        }
        return new MotechEvent(subject, params);
    }
}
