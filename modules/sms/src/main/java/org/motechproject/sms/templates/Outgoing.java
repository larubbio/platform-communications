package org.motechproject.sms.templates;

/**
 * todo
 */
public class Outgoing {
    private Request request;
    private Response response;
    private Boolean hasAuthentication;
    private Integer millisecondsBetweenMessages = 1; //at a minimum
    private Integer maxSmsSize = 160; //todo: is it fine to have this default here?
    private Integer maxRecipient = 1; //todo: and here?
    private String recipientSeparator = ","; //todo: and here?

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Boolean getHasAuthentication() {
        return hasAuthentication;
    }

    public void setHasAuthentication(Boolean hasAuthentication) {
        this.hasAuthentication = hasAuthentication;
    }

    public Integer getMillisecondsBetweenMessages() {
        return millisecondsBetweenMessages;
    }

    public void setMillisecondsBetweenMessages(Integer millisecondsBetweenMessages) {
        this.millisecondsBetweenMessages = millisecondsBetweenMessages;
    }

    public Integer getMaxSmsSize() {
        return maxSmsSize;
    }

    public void setMaxSmsSize(Integer maxSmsSize) {
        this.maxSmsSize = maxSmsSize;
    }

    public Integer getMaxRecipient() {
        return maxRecipient;
    }

    public void setMaxRecipient(Integer maxRecipient) {
        this.maxRecipient = maxRecipient;
    }

    public String getRecipientSeparator() {
        return recipientSeparator;
    }

    public void setRecipientSeparator(String recipientSeparator) {
        this.recipientSeparator = recipientSeparator;
    }

    @Override
    public String toString() {
        return "Outgoing{" +
                "request=" + request +
                ", response=" + response +
                ", hasAuthentication=" + hasAuthentication +
                ", millisecondsBetweenMessages=" + millisecondsBetweenMessages +
                ", maxSmsSize=" + maxSmsSize +
                ", maxRecipient=" + maxRecipient +
                ", recipientSeparator='" + recipientSeparator + '\'' +
                '}';
    }
}
