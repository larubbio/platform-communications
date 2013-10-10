package org.motechproject.sms.templates;

/**
 * todo
 */
public class Outgoing {
    private Request request;
    private Response response;
    private Boolean multiRecipient;
    private Integer millisecondsBetweenMessageChunks;

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

    public Integer getMillisecondsBetweenMessageChunks() {
        return millisecondsBetweenMessageChunks;
    }

    public void setMillisecondsBetweenMessageChunks(Integer millisecondsBetweenMessageChunks) {
        this.millisecondsBetweenMessageChunks = millisecondsBetweenMessageChunks;
    }
}
