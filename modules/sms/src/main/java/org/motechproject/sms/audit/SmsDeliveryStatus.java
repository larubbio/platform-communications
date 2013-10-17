package org.motechproject.sms.audit;

public enum SmsDeliveryStatus {
    UNKNOWN,
    RETRYING,
    ABORTED,
    SCHEDULED,
    PENDING,
    RECEIVED,
    DISPATCHED,
    DELIVERY_CONFIRMED
}
