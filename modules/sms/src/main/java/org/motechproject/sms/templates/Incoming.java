package org.motechproject.sms.templates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * todo
 */
public class Incoming {
    private String messageKey;
    private String senderKey;
    private String senderRegex;
    private String recipientKey;
    private String recipientRegex;
    private String timestampKey;
    private String msgIdKey;
    private Pattern pExtractSender = null;
    private Pattern pExtractRecipient = null;

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

    public Boolean hasSenderRegex() {
        return senderRegex != null && senderRegex.length() > 0;
    }

    public String extractSender(String s) {
        if (pExtractSender == null) {
            pExtractSender = Pattern.compile(senderRegex);
        }
        Matcher m = pExtractSender.matcher(s);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public void setSenderRegex(String senderRegex) {
        this.senderRegex = senderRegex;
    }

    public Boolean hasRecipientRegex() {
        return recipientRegex != null && recipientRegex.length() > 0;
    }

    public String extractRecipient(String s) {
        if (pExtractRecipient == null) {
            pExtractRecipient = Pattern.compile(recipientRegex);
        }
        Matcher m = pExtractRecipient.matcher(s);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public void setRecipientRegex(String recipientRegex) {
        this.recipientRegex = recipientRegex;
    }
}
