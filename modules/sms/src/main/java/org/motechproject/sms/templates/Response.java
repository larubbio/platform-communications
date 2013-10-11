package org.motechproject.sms.templates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * todo
 */
public class Response {
    private Boolean multiLineRecipientResponse = false;
    private String successStatus = null;
    private String successResponse = null;
    private String extractSuccessMessageIdAndRecipient = null;
    private String extractFailureMessageAndRecipient = null;
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