package org.motechproject.sms.audit;

//todo: make one task event subject per delivery status

public enum SmsDeliveryStatus {
    RETRYING,
    ABORTED,
    SCHEDULED,
    PENDING,
    RECEIVED,
    DISPATCHED,
    DELIVERY_CONFIRMED,
    FAILURE_CONFIRMED
}
