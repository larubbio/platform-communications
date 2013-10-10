package org.motechproject.sms.templates;

/**
 * todo
 */
public class Response {
    private Boolean multiLineRecipientResponse;
    private String successStatus;
    private String successResponse;
    private String successRecipient;
    private String successMessageId;
    private String failureRecipient;
    private String failureMessage;

    public String getSuccessStatus() {
        return successStatus;
    }

    public Boolean hasSuccessStatus() {
        return successStatus != null && !successStatus.isEmpty();
    }

    public void setSuccessStatus(String successStatus) {
        this.successStatus = successStatus;
    }

    public Boolean hasSuccessResponse() {
        return successResponse != null && !successResponse.isEmpty();
    }

    public String getSuccessResponse() {
        return successResponse;
    }

    public void setSuccessResponse(String successResponse) {
        this.successResponse = successResponse;
    }

    public Boolean getMultiLineRecipientResponse() {
        return multiLineRecipientResponse;
    }

    public void setMultiLineRecipientResponse(Boolean multiLineRecipientResponse) {
        this.multiLineRecipientResponse = multiLineRecipientResponse;
    }

    public Boolean hasSuccessRecipient() {
        return successRecipient != null && !successRecipient.isEmpty();
    }

    public String getSuccessRecipient() {
        return successRecipient;
    }

    public void setSuccessRecipient(String successRecipient) {
        this.successRecipient = successRecipient;
    }

    public Boolean hasSuccessMessageId() {
        return successMessageId != null && !successMessageId.isEmpty();
    }

    public String getSuccessMessageId() {
        return successMessageId;
    }

    public void setSuccessMessageId(String successMessageId) {
        this.successMessageId = successMessageId;
    }

    public Boolean hasFailureRecipient() {
        return failureRecipient != null && !failureRecipient.isEmpty();
    }

    public String getFailureRecipient() {
        return failureRecipient;
    }

    public void setFailureRecipient(String failureRecipient) {
        this.failureRecipient = failureRecipient;
    }

    public Boolean hasFailureMessage() {
        return failureMessage != null && !failureMessage.isEmpty();
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }
}
