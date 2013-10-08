package org.motechproject.sms.templates;

/**
 * todo
 */
public class Response {
    private String successStatus;
    private String successResponse;
    private String extractRefNum;

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

    public Boolean hasExtractRefNum() {
        return extractRefNum != null && !extractRefNum.isEmpty();
    }

    public String getExtractRefNum() {
        return extractRefNum;
    }

    public void setExtractRefNum(String extractRefNum) {
        this.extractRefNum = extractRefNum;
    }
}
