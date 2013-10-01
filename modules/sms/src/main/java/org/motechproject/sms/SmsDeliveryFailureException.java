package org.motechproject.sms;

/**
 * TODO
 *
 */

public class SmsDeliveryFailureException extends RuntimeException {

    public SmsDeliveryFailureException(Exception cause) {
        super(cause);
    }

    public SmsDeliveryFailureException() {
    }

    public SmsDeliveryFailureException(String message) {
        super(message);
    }

    public SmsDeliveryFailureException(String message, Exception cause) {
        super(message, cause);
    }
}
