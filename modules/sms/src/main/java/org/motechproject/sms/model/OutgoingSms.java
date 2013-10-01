package org.motechproject.sms.model;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Objects;


public class OutgoingSms {
    private List<String> recipients;
    private String message;
    private String config;
    private DateTime deliveryTime;

    public OutgoingSms() {
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
                config, recipients, message, deliveryTime);
    }
}
