package org.motechproject.sms.templates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * How to interpret provider-specific response statuses
 */
public class Status {
    private String messageIdKey;
    private String statusKey;
    private String statusSuccess;
    private String statusFailure;

    public Boolean hasMessageIdKey() {
        return messageIdKey != null && !messageIdKey.isEmpty();
    }

    public String getMessageIdKey() {
        return messageIdKey;
    }

    public void setMessageIdKey(String messageIdKey) {
        this.messageIdKey = messageIdKey;
    }

    public Boolean hasStatusKey() {
        return statusKey != null && !statusKey.isEmpty();
    }

    public String getStatusKey() {
        return statusKey;
    }

    public void setStatusKey(String statusKey) {
        this.statusKey = statusKey;
    }

    public Boolean hasStatusSuccess() {
        return statusSuccess != null && !statusSuccess.isEmpty();
    }

    public String getStatusSuccess() {
        return statusSuccess;
    }

    public void setStatusSuccess(String statusSuccess) {
        this.statusSuccess = statusSuccess;
    }

    public Boolean hasStatusFailure() {
        return statusFailure != null && !statusFailure.isEmpty();
    }

    public String getStatusFailure() {
        return statusFailure;
    }

    public void setStatusFailure(String statusFailure) {
        this.statusFailure = statusFailure;
    }

    @Override
    public String toString() {
        return "Status{" +
                "messageIdKey='" + messageIdKey + '\'' +
                ", statusKey='" + statusKey + '\'' +
                ", statusSuccess='" + statusSuccess + '\'' +
                ", statusFailure='" + statusFailure + '\'' +
                '}';
    }
}
