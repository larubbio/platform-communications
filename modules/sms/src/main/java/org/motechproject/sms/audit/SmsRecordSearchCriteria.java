package org.motechproject.sms.audit;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.couchdb.query.QueryParam;

import java.util.HashSet;
import java.util.Set;


/**
 * todo
 */
public class SmsRecordSearchCriteria {

    private Set<SmsType> SmsTypes = new HashSet<>();
    private String config;
    private String phoneNumber;
    private String messageContent;
    private Range<DateTime> timestampRange;
    private Set<SmsDeliveryStatus> smsDeliveryStatuses = new HashSet<>();
    private String motechId;
    private String providerId;
    private String errorMessage;
    private QueryParam queryParam = new QueryParam();

    public SmsRecordSearchCriteria withSmsTypes(Set<SmsType> SmsTypes) {
        this.SmsTypes.addAll(SmsTypes);
        return this;
    }

    public SmsRecordSearchCriteria withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public SmsRecordSearchCriteria withConfig(String config) {
        this.config = config;
        return this;
    }

    public SmsRecordSearchCriteria withMotechId(String motechId) {
        this.motechId = motechId;
        return this;
    }

    public SmsRecordSearchCriteria withProviderId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    public SmsRecordSearchCriteria withMessageContent(String messageContent) {
        this.messageContent = messageContent;
        return this;
    }

    public SmsRecordSearchCriteria withErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public SmsRecordSearchCriteria withTimestamp(DateTime timestamp) {
        this.timestampRange = new Range<>(timestamp, timestamp);
        return this;
    }

    public SmsRecordSearchCriteria withTimestampRange(Range<DateTime> timestampRange) {
        this.timestampRange = timestampRange;
        return this;
    }

    public SmsRecordSearchCriteria withSmsDeliveryStatuses(Set<SmsDeliveryStatus> smsDeliveryStatuses) {
        this.smsDeliveryStatuses.addAll(smsDeliveryStatuses);
        return this;
    }

    public SmsRecordSearchCriteria withQueryParam(QueryParam queryParam) {
        this.queryParam = queryParam;
        return this;
    }

    // Getters

    public Set<String> getSmsTypes() {
        return toStringSet(SmsTypes);
    }

    public String getConfig() {
        return config;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public Range<DateTime> getTimestampRange() {
        return timestampRange;
    }

    public Set<String> getSmsDeliveryStatuses() {
        return toStringSet(smsDeliveryStatuses);
    }

    public String getMotechId() {
        return motechId;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public QueryParam getQueryParam() {
        return queryParam;
    }

    private Set<String> toStringSet(Set<? extends Enum> items) {
        Set<String> itemStringSet = new HashSet<>();
        for (Enum item : items) {
            itemStringSet.add(item.name());
        }
        return itemStringSet;
    }
}
