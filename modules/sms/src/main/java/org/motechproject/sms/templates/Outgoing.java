package org.motechproject.sms.templates;

/**
 * todo
 */
public class Outgoing {
    private Request request;
    private Response response;
    private Boolean multiRecipient;
    private Boolean hasAuthentication;
    private Integer millisecondsBetweenMessages = 1; //at a minimum
    private Integer maxSmsSize = 140; //todo: is it fine to have this default here?

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

    public Boolean getMultiRecipient() {
        return multiRecipient;
    }

    public void setMultiRecipient(Boolean multiRecipient) {
        this.multiRecipient = multiRecipient;
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
}
