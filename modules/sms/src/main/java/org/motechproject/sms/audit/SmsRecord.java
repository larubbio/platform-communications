package org.motechproject.sms.audit;


import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.commons.date.util.DateUtil;

@TypeDiscriminator("doc.type === 'SmsRecord'")
public class SmsRecord extends MotechBaseDataObject {

    @JsonProperty
    private SmsType smsType;
    @JsonProperty
    private String phoneNumber;
    @JsonProperty
    private String messageContent;
    @JsonProperty
    private DateTime messageTime;
    @JsonProperty
    private SmsDeliveryStatus smsDeliveryStatus;
    @JsonProperty
    private String referenceNumber;

    public SmsRecord() {
    }

    public SmsRecord(SmsType smsType, String phoneNumber, String messageContent, DateTime messageTime, SmsDeliveryStatus smsDeliveryStatus, String referenceNumber) {
        super("SmsRecord");
        this.smsType = smsType;
        this.phoneNumber = phoneNumber;
        this.messageContent = messageContent;
        this.messageTime = messageTime;
        this.smsDeliveryStatus = smsDeliveryStatus;
        this.referenceNumber = referenceNumber;
    }

    public SmsType getSmsType() {
        return smsType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public DateTime getMessageTime() {
        return DateUtil.setTimeZoneUTC(messageTime);
    }

    public void setMessageTime(DateTime messageTime) {
        this.messageTime = messageTime;
    }

    public SmsDeliveryStatus getSmsDeliveryStatus() {
        return smsDeliveryStatus;
    }

    public void setStatus(SmsDeliveryStatus status) {
        this.smsDeliveryStatus = status;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }
}
