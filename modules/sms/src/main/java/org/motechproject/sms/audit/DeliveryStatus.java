package org.motechproject.sms.audit;

public enum DeliveryStatus {
    RETRYING,
    ABORTED,
    SCHEDULED,
    PENDING,
    RECEIVED,
    DISPATCHED,
    DELIVERY_CONFIRMED,
    FAILURE_CONFIRMED
}
