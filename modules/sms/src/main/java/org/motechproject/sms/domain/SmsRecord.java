package org.motechproject.sms.domain;


import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.sms.DeliveryStatus;
import org.motechproject.sms.SMSType;

@TypeDiscriminator("doc.type === 'SmsRecord'")
public class SmsRecord extends MotechBaseDataObject {

    @JsonProperty
    private SMSType smsType;
    @JsonProperty
    private String phoneNumber;
    @JsonProperty
    private String messageContent;
    @JsonProperty
    private DateTime messageTime;
    @JsonProperty
    private DeliveryStatus deliveryStatus;
    @JsonProperty
    private String referenceNumber;

    public SmsRecord() {
    }

    public SmsRecord(SMSType smsType, String phoneNumber, String messageContent, DateTime messageTime, DeliveryStatus deliveryStatus, String referenceNumber) {
        super("SmsRecord");
        this.smsType = smsType;
        this.phoneNumber = phoneNumber;
        this.messageContent = messageContent;
        this.messageTime = messageTime;
        this.deliveryStatus = deliveryStatus;
        this.referenceNumber = referenceNumber;
    }

    public SMSType getSmsType() {
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

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setStatus(DeliveryStatus status) {
        this.deliveryStatus = status;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }
}
