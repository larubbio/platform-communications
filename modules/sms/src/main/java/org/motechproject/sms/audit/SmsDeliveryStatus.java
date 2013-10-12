package org.motechproject.sms.audit;

public enum SmsDeliveryStatus {
    UNKNOWN,
    KEEPTRYING,
    ABORTED,
    PENDING,
    RECEIVED,
    DISPATCHED,
    DELIVERY_CONFIRMED
}
