package org.motechproject.sms.settings;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.sms.constants.SendSmsEventConstants;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class OutgoingSms {
    private List<String> recipients;
    private String message;
    private String config;
    private DateTime deliveryTime;
    private Integer failureCount = 0;

    public OutgoingSms() {
    }

    public OutgoingSms(MotechEvent event) {
        Map<String, Object> params = event.getParameters();
        config = (String) params.get(SendSmsEventConstants.CONFIG);
        recipients = (List<String>) params.get(SendSmsEventConstants.RECIPIENTS);
        message = (String) params.get(SendSmsEventConstants.MESSAGE);
        deliveryTime = (DateTime) params.get(SendSmsEventConstants.DELIVERY_TIME);
        if (params.containsKey(SendSmsEventConstants.FAILURE_COUNT)) {
            failureCount = (Integer) params.get(SendSmsEventConstants.FAILURE_COUNT);
        }
    }

    public OutgoingSms(String config, List<String> recipients, String message, DateTime deliveryTime) {
        this.recipients = recipients;
        this.message = message;
        this.config = config;
        this.deliveryTime = deliveryTime;
    }

    public OutgoingSms(String config, List<String> recipients, String message) {
        this.recipients = recipients;
        this.message = message;
        this.config = config;
    }

    public OutgoingSms(List<String> recipients, String message, DateTime deliveryTime) {
        this.recipients = recipients;
        this.message = message;
        this.deliveryTime = deliveryTime;
    }

    public OutgoingSms(List<String> recipients, String message) {

        this.recipients = recipients;
        this.message = message;
    }

    public String getConfig() {
        return config;
    }

    public Boolean hasConfig() {
        return config != null && !config.isEmpty();
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(DateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipients, message, deliveryTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final OutgoingSms other = (OutgoingSms) obj;

        return Objects.equals(this.config, other.config)
                && Objects.equals(this.recipients, other.recipients)
                && Objects.equals(this.message, other.message)
                && Objects.equals(this.deliveryTime, other.deliveryTime);
    }

    @Override
    public String toString() {
        return String.format("Sms{config='%s', recipients='%s', message='%s', deliveryTime='%s'}",
                config, recipients, message.replace("\n", "\\n"), deliveryTime);
    }
}
