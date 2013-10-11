package org.motechproject.sms.templates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * todo
 */
public class Response {
    private Boolean multiLineRecipientResponse;
    private String successStatus;
    private String successResponse;
    private String extractSuccessRecipient;
    private String extractSuccessMessageId;
    private String extractFailureRecipient;
    private String extractFailureMessage;

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

    public String extractSuccessRecipient(String response) {
        if (hasExtractSuccessRecipient()) {
            //todo: cache that at class-level
            Pattern p = Pattern.compile(extractSuccessRecipient);
            Matcher m = p.matcher(response);
            if (m.find()) {
                return m.group(1);
            }
        }
        return null;
    }

    public Boolean hasExtractSuccessRecipient() {
        return extractSuccessRecipient != null && !extractSuccessRecipient.isEmpty();
    }

    public String getExtractSuccessRecipient() {
        return extractSuccessRecipient;
    }

    public void setExtractSuccessRecipient(String extractSuccessRecipient) {
        this.extractSuccessRecipient = extractSuccessRecipient;
    }

    public String extractSuccessMessageId(String response) {
        if (hasExtractSuccessMessageId()) {
            //todo: cache that at class-level
            Pattern p = Pattern.compile(extractSuccessMessageId);
            Matcher m = p.matcher(response);
            if (m.find()) {
                return m.group(1);
            }
        }
        return null;
    }

    public Boolean hasExtractSuccessMessageId() {
        return extractSuccessMessageId != null && !extractSuccessMessageId.isEmpty();
    }

    public String getExtractSuccessMessageId() {
        return extractSuccessMessageId;
    }

    public void setExtractSuccessMessageId(String extractSuccessMessageId) {
        this.extractSuccessMessageId = extractSuccessMessageId;
    }

    public String extractFailureRecipient(String response) {
        if (hasExtractFailureRecipient()) {
            //todo: cache that at class-level
            Pattern p = Pattern.compile(extractFailureRecipient);
            Matcher m = p.matcher(response);
            if (m.find()) {
                return m.group(1);
            }
        }
        return null;
    }

    public Boolean hasExtractFailureRecipient() {
        return extractFailureRecipient != null && !extractFailureRecipient.isEmpty();
    }

    public String getExtractFailureRecipient() {
        return extractFailureRecipient;
    }

    public void setExtractFailureRecipient(String extractFailureRecipient) {
        this.extractFailureRecipient = extractFailureRecipient;
    }

    public String extractFailureMessage(String response) {
        if (hasExtractFailureMessage()) {
            //todo: cache that at class-level
            Pattern p = Pattern.compile(extractFailureMessage);
            Matcher m = p.matcher(response);
            if (m.find()) {
                return m.group(1);
            }
        }
        return null;
    }

    public Boolean hasExtractFailureMessage() {
        return extractFailureMessage != null && !extractFailureMessage.isEmpty();
    }

    public String getExtractFailureMessage() {
        return extractFailureMessage;
    }

    public void setExtractFailureMessage(String extractFailureMessage) {
        this.extractFailureMessage = extractFailureMessage;
    }
}
