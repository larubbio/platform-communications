package org.motechproject.sms.templates;

//todo: handle malformed template files (ie: resulting in exceptions in the regex parsing) in a useful way for implementers?

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * How to to deal with provider-specific http responses
 */
public class Response {
    private Boolean multiLineRecipientResponse = false;
    private Boolean singleRecipientResponse = false;
    private String successStatus = null;
    private String successResponse = null;
    private String extractSingleSuccessMessageId = null;
    private String extractSingleFailureMessage = null;
    private String extractGeneralFailureMessage = null;
    private String extractSuccessMessageIdAndRecipient = null;
    private String extractFailureMessageAndRecipient = null;
    private String headerMessageId = null;
    Pattern pSuccessResponse = null;
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
        if (pSuccessResponse == null) {
            pSuccessResponse = Pattern.compile(successResponse, Pattern.DOTALL);
        }
        Matcher matcher = pSuccessResponse.matcher(response);
        Boolean ret = matcher.matches();
        return ret;
    }

    public Boolean supportsMultiLineRecipientResponse() {
        return multiLineRecipientResponse;
    }

    public Boolean supportsSingleRecipientResponse() {
        return singleRecipientResponse;
    }

    public Boolean hasSingleSuccessMessageId() {
        return extractSingleSuccessMessageId != null && extractSingleSuccessMessageId.length() > 0;
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
        if (extractSingleFailureMessage != null && extractSingleFailureMessage.length() > 0) {
            if (pExtractSingleFailureMessage == null) {
                pExtractSingleFailureMessage = Pattern.compile(extractSingleFailureMessage);
            }
            Matcher m = pExtractSingleFailureMessage.matcher(response);
            if (m.find()) {
                return m.group(1);
            }
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

    //todo: what if bad or wrong number of regex groups ? ie (only one extract)
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

    public Boolean hasHeaderMessageId() {
        return headerMessageId != null && !headerMessageId.isEmpty();
    }

    public String getHeaderMessageId() {
        return headerMessageId;
    }

    @Override
    public String toString() {
        return "Response{" +
                "headerMessageId='" + headerMessageId + '\'' +
                ", multiLineRecipientResponse=" + multiLineRecipientResponse +
                ", singleRecipientResponse=" + singleRecipientResponse +
                ", successStatus='" + successStatus + '\'' +
                ", successResponse='" + successResponse + '\'' +
                ", extractSingleSuccessMessageId='" + extractSingleSuccessMessageId + '\'' +
                ", extractSingleFailureMessage='" + extractSingleFailureMessage + '\'' +
                ", extractGeneralFailureMessage='" + extractGeneralFailureMessage + '\'' +
                ", extractSuccessMessageIdAndRecipient='" + extractSuccessMessageIdAndRecipient + '\'' +
                ", extractFailureMessageAndRecipient='" + extractFailureMessageAndRecipient + '\'' +
                '}';
    }
}