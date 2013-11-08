package org.motechproject.sms.audit;

/**
 * Cross-provider delivery statuses
 */
public enum DeliveryStatus {
    RETRYING, // There was a problem delivering a message to the provider, we're retrying
    ABORTED, // The maximum number of retries has occurred, so we've given up trying
    SCHEDULED, // This message was added to the MOTECH schedule
    PENDING, // We just received a message and will either try to send it directly or schedule it for later delivery
    RECEIVED, // Incoming SMS
    DISPATCHED, // SMS was successfully sent to the provider
    DELIVERY_CONFIRMED, // Received confirmation from the provider that the message was delivered
    FAILURE_CONFIRMED // Received confirmation from the provider that the message was not delivered
}
