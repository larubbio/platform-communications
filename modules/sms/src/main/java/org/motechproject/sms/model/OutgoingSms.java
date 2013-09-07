package org.motechproject.sms.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.joda.time.DateTime;
import org.motechproject.sms.json.OutgoingSmsDeserializer;

import java.util.List;
import java.util.Objects;

@JsonDeserialize(using = OutgoingSmsDeserializer.class)
public class OutgoingSms {
    private List<String> recipients;
    private String message;
    private DateTime deliveryTime;

    public OutgoingSms(List<String> recipients, String message, DateTime deliveryTime) {
        this.recipients = recipients;
        this.message = message;
        this.deliveryTime = deliveryTime;
    }

    public OutgoingSms(List<String> recipients, String message) {
        this.recipients = recipients;
        this.message = message;
        this.deliveryTime = null;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public String getMessage() {
        return message;
    }

    public DateTime getAt() {
        return deliveryTime;
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

        return Objects.equals(this.recipients, other.recipients)
                && Objects.equals(this.message, other.message)
                && Objects.equals(this.deliveryTime, other.deliveryTime);
    }

    @Override
    public String toString() {
        return String.format("Sms{recipients='%s', message='%s', deliveryTime='%s'}", recipients, message, deliveryTime);
    }
}
