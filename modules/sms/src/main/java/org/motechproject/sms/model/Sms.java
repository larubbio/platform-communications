package org.motechproject.sms.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.motechproject.sms.json.SmsDeserializer;

import java.util.Objects;

@JsonDeserialize(using = SmsDeserializer.class)
public class Sms {
    private String from;
    private String to;
    private String message;

    public Sms(String from, String to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }

    public String getFromAddress() {
        return from;
    }

    public String getToAddress() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, message);
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

        return Objects.equals(this.from, other.from)
                && Objects.equals(this.to, other.to)
                && Objects.equals(this.message, other.message);
    }

    @Override
    public String toString() {
        return String.format(
                "Sms{from='%s', to='%s', message='%s'}",
                from, to, message);
    }
}
