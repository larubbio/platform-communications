package org.motechproject.sms.audit;

public enum SmsDeliveryStatus {
    UNKNOWN,
    KEEPTRYING,
    ABORTED,
    SCHEDULED,
    PENDING,
    RECEIVED,
    DISPATCHED,
    DELIVERY_CONFIRMED
}
