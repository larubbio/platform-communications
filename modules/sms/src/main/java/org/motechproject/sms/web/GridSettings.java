package org.motechproject.sms.web;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.couchdb.query.QueryParam;
import org.motechproject.sms.audit.SmsDeliveryStatus;
import org.motechproject.sms.audit.SmsRecordSearchCriteria;
import org.motechproject.sms.audit.SmsType;

import java.util.HashSet;
import java.util.Set;

/**
 * todo
 */
public class GridSettings {

    private Integer rows;
    private Integer page;
    private String sortColumn;
    private String sortDirection;
    private String config;
    private String phoneNumber;
    private String messageContent;
    private String timeFrom;
    private String timeTo;
    private String smsDeliveryStatus;
    private String smsType;
    private String motechId;
    private String providerId;

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public String getSmsDeliveryStatus() {
        return smsDeliveryStatus;
    }

    public void setSmsDeliveryStatus(String smsDeliveryStatus) {
        this.smsDeliveryStatus = smsDeliveryStatus;
    }

    public String getSmsType() {
        return smsType;
    }

    public void setSmsType(String smsType) {
        this.smsType = smsType;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public SmsRecordSearchCriteria toSmsRecordSearchCriteria() {
        boolean reverse = "desc".equalsIgnoreCase(sortDirection);
        QueryParam queryParam = new QueryParam(page - 1, rows, sortColumn, reverse);
        Set<SmsType> types = getSmsTypeFromSettings();
        Set<SmsDeliveryStatus> deliveryStatusList = getSmsDeliveryStatusFromSettings();
        Range<DateTime> range = createRangeFromSettings();
        SmsRecordSearchCriteria criteria = new SmsRecordSearchCriteria();
        if (!types.isEmpty()) {
            criteria.withSmsTypes(types);
        }
        if (!deliveryStatusList.isEmpty()) {
            criteria.withSmsDeliveryStatuses(deliveryStatusList);
        }
        if (StringUtils.isNotBlank(config)) {
            criteria.withConfig(config + "*");
        }
        if (StringUtils.isNotBlank(phoneNumber)) {
            criteria.withPhoneNumber(phoneNumber + "*");
        }
        if (StringUtils.isNotBlank(messageContent)) {
            criteria.withMessageContent(messageContent + "*");
        }
        if (StringUtils.isNotBlank(motechId)) {
            criteria.withMotechId(motechId + "*");
        }
        if (StringUtils.isNotBlank(providerId)) {
            criteria.withProviderId(providerId + "*");
        }
        criteria.withTimestampRange(range);
        criteria.withQueryParam(queryParam);
        return criteria;
    }

    private Set<SmsType> getSmsTypeFromSettings() {
        Set<SmsType> smsTypes = new HashSet<>();
        String[] smsTypeList = smsType.split(",");
        for (String type : smsTypeList) {
            if (!type.isEmpty()) {
                smsTypes.add(SmsType.valueOf(type));
            }
        }
        return smsTypes;
    }

    private Set<SmsDeliveryStatus> getSmsDeliveryStatusFromSettings() {
        Set<SmsDeliveryStatus> statusList = new HashSet<>();
        String[] statuses = smsDeliveryStatus.split(",");
        for (String status : statuses) {
            if (!status.isEmpty()) {
                statusList.add(SmsDeliveryStatus.valueOf(status));
            }
        }
        return statusList;
    }

    private Range<DateTime> createRangeFromSettings() {
        DateTime from;
        DateTime to;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isNotBlank(timeFrom)) {
            from = formatter.parseDateTime(timeFrom);
        } else {
            from = new DateTime(0);
        }
        if (StringUtils.isNotBlank(timeTo)) {
            to = formatter.parseDateTime(timeTo);
        } else {
            to = DateTime.now();
        }
        return new Range<DateTime>(from, to);
    }
}
