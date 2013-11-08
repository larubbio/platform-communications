package org.motechproject.sms.alert;

/**
 * Helper class - Uses StatusMessageService to send system Alerts
 */
public interface MotechStatusMessage {

    public void alert(String message);
}
