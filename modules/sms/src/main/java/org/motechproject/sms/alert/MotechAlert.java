package org.motechproject.sms.alert;

/**
 * Helper class - Uses StatusMessageService to send system Alerts
 */
public interface MotechAlert {

    public void alert(String message);
}
