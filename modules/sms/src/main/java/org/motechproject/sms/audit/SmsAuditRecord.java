package org.motechproject.sms.audit;


import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.commons.date.util.DateUtil;

/**
 * todo
 */
@TypeDiscriminator("doc.type === 'SmsAuditRecord'")
public class SmsAuditRecord extends MotechBaseDataObject {

    @JsonProperty
    private SmsType smsType;
    @JsonProperty
    private String phoneNumber;
    @JsonProperty
    private String messageContent;
    @JsonProperty
    private DateTime messageTime;
    @JsonProperty
    private SmsDeliveryStatus deliveryStatus;
    @JsonProperty
    private String referenceNumber;

    public SmsAuditRecord() {
    }

    public SmsAuditRecord(SmsType smsType, String phoneNumber, String messageContent, DateTime messageTime, SmsDeliveryStatus deliveryStatus, String referenceNumber) {
        super("SmsAuditRecord");
        this.smsType = smsType;
        this.phoneNumber = phoneNumber;
        this.messageContent = messageContent;
        this.messageTime = messageTime;
        this.deliveryStatus = deliveryStatus;
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

    public SmsDeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setStatus(SmsDeliveryStatus status) {
        this.deliveryStatus = status;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }
}
