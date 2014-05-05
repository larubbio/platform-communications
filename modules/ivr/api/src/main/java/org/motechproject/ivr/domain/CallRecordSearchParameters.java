package org.motechproject.ivr.domain;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.util.QueryParams;

import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class CallRecordSearchParameters {
    private static final String DEFAULT_DATE_FORMAT = "YYYY-MM-dd HH:mm:ss";
    private String phoneNumber;

    private DateTime startFromDate;
    private DateTime startToDate;

    private DateTime answerFromDate;
    private DateTime answerToDate;

    private DateTime endFromDate;
    private DateTime endToDate;

    private Integer minDuration;
    private Integer maxDuration;


    private boolean answered;
    private boolean busy;
    private boolean failed;
    private boolean noAnswer;
    private boolean unknown;

    private boolean inbound;
    private boolean outbound;

    // QueryParams holds page number to return, records per page, what column to sort by, and whether or not to sort in
    // reverse order
    private QueryParams queryParam;

    public void setQueryParams(QueryParams queryParams) {
        this.queryParam = queryParams;
    }

    public QueryParams getQueryParams() {
        return queryParam;
    }

    public void setInbound(boolean inbound) {
        this.inbound = inbound;
    }

    public boolean isInbound() {
        return inbound;
    }

    public Set<CallDirection> getDirections() {
        Set<CallDirection> directions = new HashSet<>();
        if (inbound) { directions.add(CallDirection.INBOUND); }
        if (outbound) { directions.add(CallDirection.OUTBOUND); }
        return directions;
    }

    public Set<CallDisposition> getDispositions() {
        Set<CallDisposition> dispositions = new HashSet<>();
        if (answered) { dispositions.add(CallDisposition.ANSWERED); }
        if (busy) { dispositions.add(CallDisposition.BUSY); }
        if (failed) { dispositions.add(CallDisposition.FAILED); }
        if (noAnswer) { dispositions.add(CallDisposition.NO_ANSWER); }
        if (unknown) { dispositions.add(CallDisposition.UNKNOWN); }
        return dispositions;
    }

    public void setOutbound(boolean outbound) {
        this.outbound = outbound;
    }

    public boolean isOutbound() {
        return outbound;
    }

    public String getStartToDate() {
        return startToDate.toString(DEFAULT_DATE_FORMAT);
    }

    public DateTime getStartToDateAsDateTime() {
        return startToDate;
    }

    public void setStartToDate(String toDate) {
        this.startToDate = toDate(toDate);
    }

    private DateTime toDate(String date) {
        return isNotBlank(date) ? DateTime.parse(date, DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT)) : null;
    }

    public String getStartFromDate() {
        return startFromDate.toString(DEFAULT_DATE_FORMAT);
    }

    public Range<DateTime> getStartTimeRange() { return new Range<DateTime>(startFromDate, startToDate); }
    public Range<DateTime> getAnswerTimeRange() { return new Range<DateTime>(answerFromDate, answerToDate); }
    public Range<DateTime> getEndTimeRange() { return new Range<DateTime>(endFromDate, endToDate); }

    public DateTime getStartFromDateAsDateTime() {
        return startFromDate;
    }

    public void setStartFromDate(String fromDate) {
        this.startFromDate = toDate(fromDate);
    }

    public void setStartFromDate(DateTime date) {
        this.startFromDate = date;
    }

    public void setStartToDate(DateTime date) {
        this.startToDate = date;
    }


    public void setEndFromDate(DateTime date) {
        this.endFromDate = date;
    }

    public void setEndToDate(DateTime date) {
        this.endToDate = date;
    }


    public String getAnswerToDate() {
        return answerToDate.toString(DEFAULT_DATE_FORMAT);
    }

    public DateTime getAnswerToDateAsDateTime() {
        return answerToDate;
    }

    public void setAnswerToDate(String toDate) {
        this.answerToDate = toDate(toDate);
    }

    public String getAnswerFromDate() {
        return answerFromDate.toString(DEFAULT_DATE_FORMAT);
    }

    public DateTime getAnswerFromDateAsDateTime() {
        return answerFromDate;
    }

    public void setAnswerFromDate(String fromDate) {
        this.answerFromDate = toDate(fromDate);
    }


    public String getEndToDate() {
        return endToDate.toString(DEFAULT_DATE_FORMAT);
    }

    public DateTime getEndToDateAsDateTime() {
        return endToDate;
    }

    public void setEndToDate(String toDate) {
        this.endToDate = toDate(toDate);
    }

    public String getEndFromDate() {
        return endFromDate.toString(DEFAULT_DATE_FORMAT);
    }

    public DateTime getEndFromDateAsDateTime() {
        return endFromDate;
    }

    public void setEndFromDate(String fromDate) {
        this.endFromDate = toDate(fromDate);
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Range<Integer> getDurationRange() { return new Range<Integer>(minDuration, maxDuration); }

    public Integer getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(Integer maxDuration) {
        this.maxDuration = maxDuration;
    }

    public Integer getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(Integer minDuration) {
        this.minDuration = minDuration;
    }

    public boolean getAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public boolean getBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public boolean getFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public boolean getNoAnswer() {
        return noAnswer;
    }

    public void setNoAnswer(boolean noAnswer) {
        this.noAnswer = noAnswer;
    }

    public boolean getUnknown() {
        return unknown;
    }

    public void setUnknown(boolean unknown) {
        this.unknown = unknown;
    }
}
