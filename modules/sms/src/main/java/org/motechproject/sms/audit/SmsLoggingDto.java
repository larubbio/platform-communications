package org.motechproject.sms.audit;

import org.joda.time.format.DateTimeFormat;
import org.motechproject.commons.date.util.DateUtil;

/**
 * todo
 */
public class SmsLoggingDto {

    private String phoneNumber;
    private String smsType;
    private String timestamp;
    private String smsDeliveryStatus;
    private String messageContent;

    public SmsLoggingDto(SmsRecord record) {
        this.phoneNumber = record.getPhoneNumber();
        this.smsType = record.getSmsType().toString();
        // DateUtil.setTimeZone converts the message time from UTC to local time for display
        this.timestamp = DateTimeFormat.forPattern("Y-MM-dd hh:mm:ss").print(DateUtil.setTimeZone(record.getTimestamp()));
        this.smsDeliveryStatus = record.getSmsDeliveryStatus().toString();
        this.messageContent = record.getMessageContent();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSmsType() {
        return smsType;
    }

    public void setSmsType(String smsType) {
        this.smsType = smsType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSmsDeliveryStatus() {
        return smsDeliveryStatus;
    }

    public void setDeliveryStatus(String smsDeliveryStatus) {
        this.smsDeliveryStatus = smsDeliveryStatus;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

}
