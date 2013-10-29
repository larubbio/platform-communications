package org.motechproject.sms.audit;

//todo: make one task event subject per delivery status
//todo: motech statuses: scheduled, pending, failed_to_deliver_to_gateway, delivered_to_gateway, failure_confirmed, delivery_confirmed

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
