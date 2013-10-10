package org.motechproject.sms.templates;

/**
 * todo
 */
public class Incoming {
    private String messageKey;
    private String senderKey;
    private String recipientKey;
    private String timestampKey;
    private String msgIdKey;
    private String response;

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

    public String getRecipientKey() {
        return recipientKey;
    }

    public void setRecipientKey(String recipientKey) {
        this.recipientKey = recipientKey;
    }

    public String getTimestampKey() {
        return timestampKey;
    }

    public void setTimestampKey(String timestampKey) {
        this.timestampKey = timestampKey;
    }

    public String getMsgIdKey() {
        return msgIdKey;
    }

    public void setMsgIdKey(String msgIdKey) {
        this.msgIdKey = msgIdKey;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
