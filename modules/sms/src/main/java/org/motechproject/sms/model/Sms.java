package org.motechproject.sms.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.joda.time.DateTime;
import org.motechproject.sms.json.SmsDeserializer;

import java.util.List;
import java.util.Objects;

@JsonDeserialize(using = SmsDeserializer.class)
public class Sms {
    private List<String> recipients;
    private String message;
    private DateTime at;

    public Sms(List<String> recipients, String message, DateTime at) {
        this.recipients = recipients;
        this.message = message;
        this.at = at;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public String getMessage() {
        return message;
    }

    public DateTime getAt() {
        return at;
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipients, message, at);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Sms other = (Sms) obj;

        return Objects.equals(this.recipients, other.recipients)
                && Objects.equals(this.message, other.message)
                && Objects.equals(this.at, other.at);
    }

    @Override
    public String toString() {
        return String.format("Sms{recipients='%s', message='%s', at='%s'}", recipients, message, at);
    }
}
