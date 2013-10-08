package org.motechproject.sms.event;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmsEvents {

    public static final String SEND_SMS = "send_sms";
    public static final String OUTBOUND_SMS_SUCCESS = "outbound_sms_success";
    public static final String OUTBOUND_SMS_FAILURE = "outbound_sms_failure";
    public static final String CONFIG = "config";
    public static final String RECIPIENTS = "recipients";
    public static final String MESSAGE = "message";
    public static final String DELIVERY_TIME = "delivery_time";
    public static final String FAILURE_COUNT = "failure_count";

    public static MotechEvent makeSendEvent(String config, List<String> recipients, String message) {
        return makeEvent(SEND_SMS, config, recipients, message, null, null);
    }

    public static MotechEvent makeSendEvent(String config, List<String> recipients, String message, DateTime deliveryTime) {
        return makeEvent(SEND_SMS, config, recipients, message, deliveryTime, null);
    }

    public static MotechEvent makeSendEvent(String config, List<String> recipients, String message, DateTime deliveryTime, Integer failureCount) {
        return makeEvent(SEND_SMS, config, recipients, message, deliveryTime, failureCount);
    }

    public static MotechEvent makeOutboundSmsFailureEvent(String config, List<String> recipients, String message, DateTime deliveryTime, Integer failureCount) {
        return makeEvent(OUTBOUND_SMS_FAILURE, config, recipients, message, deliveryTime, failureCount);
    }

    public static MotechEvent makeOutboundSmsSuccessEvent(String config, List<String> recipients, String message, DateTime deliveryTime, Integer failureCount) {
        return makeEvent(OUTBOUND_SMS_SUCCESS, config, recipients, message, deliveryTime, failureCount);
    }

    private static MotechEvent makeEvent(String subject, String config, List<String> recipients, String message, DateTime deliveryTime, Integer failureCount) {
        Map<String, Object> params = new HashMap<>();
        params.put(CONFIG, config);
        params.put(RECIPIENTS, recipients);
        params.put(MESSAGE, message);
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
