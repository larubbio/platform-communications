package org.motechproject.sms.templates;

//todo: handle malformed template files (ie: resulting in exceptions in the regex parsing) in a useful way for implementers

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * todo
 */
public class Response {
    private Boolean multiLineRecipientResponse = false;
    private Boolean singleRecipientResponse = false;
    private String successStatus = null;
    private String successResponse = null; //todo: compile this guy just like the others below
    private String extractSingleSuccessMessageId = null;
    private String extractSingleFailureMessage = null;
    private String extractGeneralFailureMessage = null;
    private String extractSuccessMessageIdAndRecipient = null;
    private String extractFailureMessageAndRecipient = null;
    Pattern pExtractSingleSuccessMessageId = null;
    Pattern pExtractSingleFailureMessage = null;
    Pattern pExtractGeneralFailureMessage = null;
    Pattern pExtractSuccessMessageIdAndRecipient = null;
    Pattern pExtractFailureMessageAndRecipient = null;

    public Boolean hasSuccessStatus() {
        return successStatus != null && !successStatus.isEmpty();
    }

    public Boolean checkSuccessStatus(Integer status) {
        return status.toString().matches(successStatus);
    }

    public Boolean hasSuccessResponse() {
        return successResponse != null && !successResponse.isEmpty();
    }

    public Boolean checkSuccessResponse(String response) {
        return response.matches(successResponse);
    }

    public Boolean supportsMultiLineRecipientResponse() {
        return multiLineRecipientResponse;
    }

    public Boolean supportsSingleRecipientResponse() {
        return singleRecipientResponse;
    }

    public String extractSingleSuccessMessageId(String response) {
        if (pExtractSingleSuccessMessageId == null) {
            pExtractSingleSuccessMessageId = Pattern.compile(extractSingleSuccessMessageId);
        }
        Matcher m = pExtractSingleSuccessMessageId.matcher(response);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public String extractSingleFailureMessage(String response) {
        if (pExtractSingleFailureMessage == null) {
            pExtractSingleFailureMessage = Pattern.compile(extractSingleFailureMessage);
        }
        Matcher m = pExtractSingleFailureMessage.matcher(response);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public String extractGeneralFailureMessage(String response) {
        if (pExtractGeneralFailureMessage == null) {
            pExtractGeneralFailureMessage = Pattern.compile(extractGeneralFailureMessage);
        }
        Matcher m = pExtractGeneralFailureMessage.matcher(response);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public String[] extractSuccessMessageIdAndRecipient(String response) {
        if (pExtractSuccessMessageIdAndRecipient == null) {
            pExtractSuccessMessageIdAndRecipient = Pattern.compile(extractSuccessMessageIdAndRecipient);
        }
        Matcher m = pExtractSuccessMessageIdAndRecipient.matcher(response);
        if (m.find()) {
            return new String[] {m.group(1), m.group(2)};
        }
        return null;
    }

    public String[] extractFailureMessageAndRecipient(String response) {
        if (pExtractFailureMessageAndRecipient == null) {
            pExtractFailureMessageAndRecipient = Pattern.compile(extractFailureMessageAndRecipient);
        }
        Matcher m = pExtractFailureMessageAndRecipient.matcher(response);
        if (m.find()) {
            return new String[] {m.group(1), m.group(2)};
        }
        return null;
    }
}