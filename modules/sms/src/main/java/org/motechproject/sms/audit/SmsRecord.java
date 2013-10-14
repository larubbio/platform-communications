package org.motechproject.sms.audit;


import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.commons.date.util.DateUtil;

@TypeDiscriminator("doc.type === 'SmsRecord'")
public class SmsRecord extends MotechBaseDataObject {

    @JsonProperty
    private String config;
    @JsonProperty
    private SmsType smsType;
    @JsonProperty
    private String phoneNumber;
    @JsonProperty
    private String messageContent;
    @JsonProperty
    private DateTime timestamp;
    @JsonProperty
    private SmsDeliveryStatus smsDeliveryStatus;
    @JsonProperty
    private String motechId;
    @JsonProperty
    private String providerId;

    public SmsRecord() {
    }

    public SmsRecord(String config, SmsType smsType, String phoneNumber, String messageContent, DateTime timestamp,
                     SmsDeliveryStatus smsDeliveryStatus, String motechId, String providerId) {
        super("SmsRecord");
        this.config = config;
        this.smsType = smsType;
        this.phoneNumber = phoneNumber;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
        this.smsDeliveryStatus = smsDeliveryStatus;
        this.motechId = motechId;
        this.providerId = providerId;
    }

    public String getConfig() {
        return config;
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

    public DateTime getTimestamp() {
        return DateUtil.setTimeZoneUTC(timestamp);
    }

    public SmsDeliveryStatus getSmsDeliveryStatus() {
        return smsDeliveryStatus;
    }

    public String getMotechId() {
        return motechId;
    }

    public String getProviderId() {
        return providerId;
    }

    public String toString() {
        return String.format("config: %s, smsType: %s, number: %s, motechId: %s, providerId: %s, timestamp: %s, deliveryStatus: %s",
            config, smsType.toString(), phoneNumber, motechId, providerId, timestamp, smsDeliveryStatus);
    }
}
