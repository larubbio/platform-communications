package org.motechproject.ivr.domain;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.util.Date;

import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.commons.date.util.DateUtil.setTimeZoneUTC;


/**
 * Call Detail Record represents call events and data captured in a call along with call metrics.
 */
@Entity
public class CallDetailRecord implements CallDetail {

    @Field
    private DateTime startDate;
    @Field
    private DateTime endDate;
    @Field
    private Date answerDate;
    @Field
    private CallDisposition disposition;
    @Field
    private String errorMessage;
    @Field
    private String phoneNumber;
    @Field
    private String callId;
    @Field
    private Integer duration;
    @Field
    private CallDirection callDirection;

    private CallDetailRecord() {
    }

    public CallDetailRecord(String callId, String phoneNumber) {
        this.callId = callId;
        this.phoneNumber = phoneNumber;
        this.startDate = now();
    }

    /**
     * Constructor to create CallDetailRecord
     *
     * @param startDate
     * @param endDate
     * @param answerDate
     * @param disposition
     * @param duration
     */
    public CallDetailRecord(Date startDate, Date endDate, Date answerDate, CallDisposition disposition, Integer duration) {
        this.startDate = startDate != null ? newDateTime(startDate) : null;
        this.endDate = endDate != null ? newDateTime(endDate) : null;
        this.answerDate = answerDate;
        this.disposition = disposition;
        this.duration = duration;
    }

    /**
     * CallDetailRecord constructor for failed calls
     *
     * @param disposition: Status of call
     * @param errorMessage
     */
    public CallDetailRecord(CallDisposition disposition, String errorMessage) {
        this.errorMessage = errorMessage;
        this.disposition = disposition;
    }

    /**
     * Creates a call details record for given phone number and call details
     *
     * @param phoneNumber:   phone number of user.
     * @param callDirection: Incoming/outgoing
     * @param disposition:   Call status (busy, failed etc)
     * @return
     */
    public static CallDetailRecord create(String phoneNumber, CallDirection callDirection, CallDisposition disposition) {
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.startDate = now();
        callDetailRecord.disposition = disposition;
        callDetailRecord.answerDate = callDetailRecord.startDate.toDate();
        callDetailRecord.phoneNumber = phoneNumber;
        callDetailRecord.callDirection = callDirection;
        return callDetailRecord;
    }

    public String getCallId() {
        return callId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public DateTime getStartDate() {
        return setTimeZoneUTC(startDate);
    }

    @Override
    public DateTime getEndDate() {
        return setTimeZoneUTC(endDate);
    }

    public Date getAnswerDate() {
        return answerDate != null ? setTimeZoneUTC(newDateTime(answerDate)).toDate() : answerDate;
    }

    public CallDisposition getDisposition() {
        return disposition;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


    public CallDirection getCallDirection() {
        return callDirection;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCallDirection(CallDirection callDirection) {
        this.callDirection = callDirection;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
        duration = new Period(startDate, endDate).toStandardSeconds().getSeconds();
    }

    public void setAnswerDate(Date answerDate) {
        this.answerDate = answerDate;
    }

    public void setDisposition(CallDisposition disposition) {
        this.disposition = disposition;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
