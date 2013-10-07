package org.motechproject.sms.templates;

/**
 * todo
 */
public class Incoming {
    private String messageKey;
    private String senderKey;
    private String timestampKey;

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getSenderKey() {
        return senderKey;
    }

    public void setSenderKey(String senderKey) {
        this.senderKey = senderKey;
    }

    public String getTimestampKey() {
        return timestampKey;
    }

    public void setTimestampKey(String timestampKey) {
        this.timestampKey = timestampKey;
    }
}
