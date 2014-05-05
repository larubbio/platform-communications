package org.motechproject.callflow.repository;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ivr.domain.*;
import org.motechproject.ivr.service.CallDetailRecordService;
import org.motechproject.ivr.service.IVRDataService;
import org.motechproject.mds.util.Order;
import org.motechproject.mds.util.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests functionality of CallDetailRecordService.
 * Assumes the database is empty before tests
 * (if it's not, the shouldFindMaxCallDuration test may fail).
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class AllCallDetailRecordsIT {

    public static final String PHONE_NUMBER_1 = "99991234561";
    public static final String PHONE_NUMBER_2 = "99991234671";

    private static final int MAX_CALL_DURATION = 50;

    @Autowired
    CallDetailRecordService allCallDetailRecords;
    IVRDataService ivrDataService;

    @Before
    public void setUp() {
        allCallDetailRecords.create(getRecord(PHONE_NUMBER_1, MAX_CALL_DURATION - 10));
        allCallDetailRecords.create(getRecord(PHONE_NUMBER_2, MAX_CALL_DURATION));
    }

    private CallDetailRecord getRecord(String phoneNumber, int duration) {
        final CallDetailRecord log = new CallDetailRecord("1", phoneNumber);
        log.setAnswerDate(DateUtil.now().toDate());
        log.setStartDate(DateUtil.now());
        log.setEndDate(DateUtil.now());
        log.setDuration(duration);
        log.setDisposition(CallDisposition.UNKNOWN);
        log.setCallDirection(CallDirection.INBOUND);
        return log;
    }

    /**
     * Tests that record added to database is found via search.
     * @throws Exception
     */
    @Test
    public void shouldSearchCalllogs() throws Exception {
        DateTime endTime = DateTime.now().plusDays(1);
        DateTime startTime = DateTime.now().minusDays(1);
        int maxDuration = MAX_CALL_DURATION;
        final List<CallDetailRecord> rowList = allCallDetailRecords.findByCriteria(PHONE_NUMBER_1,
                new Range(startTime, startTime), null, new Range(endTime, endTime), new Range(0, maxDuration),
                new HashSet<CallDisposition>(Arrays.asList(CallDisposition.UNKNOWN)),
                new HashSet<CallDirection>(Arrays.asList(CallDirection.INBOUND)));
        assertTrue(rowList.size() > 0);
    }

    @Test
    public void shouldSearchCallsWithSpecificDuration() throws Exception {
        final List<CallDetailRecord> rowList = allCallDetailRecords.findByCriteria(null,
                new Range(DateTime.now().minusDays(1), DateTime.now().plusDays(1)), null, null, null, null, null);
        assertTrue(rowList.size() > 0);
    }

    @Test
    public void shouldReturnBasedOnGivenSortByParamInDescendingOrder() throws Exception {
        final CallRecordSearchParameters searchParameters = new CallRecordSearchParameters();
        searchParameters.setPhoneNumber("99991234*");
        searchParameters.setEndFromDate(DateTime.now().minusDays(1));
        searchParameters.setEndToDate(DateTime.now().plusDays(1));
        searchParameters.setQueryParams(new QueryParams(new Order("phone", Order.Direction.DESC)));
        final List<CallDetailRecord> rowList = ivrDataService.search(searchParameters);
        assertEquals(rowList.get(0).getPhoneNumber(), PHONE_NUMBER_2);
    }

    @Test
    public void shouldReturnBasedOnGivenSortByParamInAscendingOrder() throws Exception {
        final CallRecordSearchParameters searchParameters = new CallRecordSearchParameters();
        searchParameters.setPhoneNumber("99991234*");
        searchParameters.setEndFromDate(DateTime.now().minusDays(1));
        searchParameters.setEndToDate(DateTime.now().plusDays(1));
        searchParameters.setQueryParams(new QueryParams(new Order("phone", Order.Direction.ASC)));
        final List<CallDetailRecord> rowList = ivrDataService.search(searchParameters);
        assertEquals(rowList.get(0).getPhoneNumber(), PHONE_NUMBER_1);
    }

    @Test
    public void shouldReturnTheTotalNumberOfCallRecords() {
        final CallRecordSearchParameters searchParameters = new CallRecordSearchParameters();
        searchParameters.setPhoneNumber("99991234*");
        searchParameters.setEndFromDate(DateTime.now().minusDays(1));
        searchParameters.setEndToDate(DateTime.now().plusDays(1));
        long count = ivrDataService.count(searchParameters);
        assertEquals(2, count);
    }

    @Test
    public void shouldFindMaxCallDuration() {
        assertEquals(MAX_CALL_DURATION, ivrDataService.findMaxCallDuration());
    }

    @Test
    public void shouldUpdateExistingCallRecords() {
        CallDetailRecord cdr = new CallDetailRecord("callId", PHONE_NUMBER_1);
        allCallDetailRecords.create(cdr);

        List<CallDetailRecord> cdrs = allCallDetailRecords.findByCallId("callId");
        assertNotNull(cdrs);
        assertEquals(1, cdrs.size());
        cdr = cdrs.get(0);
        assertEquals("callId", cdr.getCallId());
        assertEquals(PHONE_NUMBER_1, cdr.getPhoneNumber());

        cdr = new CallDetailRecord("callId", PHONE_NUMBER_2);
        allCallDetailRecords.update(cdr);

        cdrs = allCallDetailRecords.findByCallId("callId");
        assertNotNull(cdrs);
        assertEquals(1, cdrs.size());
        cdr = cdrs.get(0);
        assertEquals(PHONE_NUMBER_2, cdr.getPhoneNumber());
    }

    @After
    public void tearDown() {
        final List<CallDetailRecord> cdrs = allCallDetailRecords.findByCriteria(PHONE_NUMBER_1,
                new Range(DateTime.now().minusDays(1), DateTime.now().plusDays(1)), null, null, null, null, null);
        cdrs.addAll(allCallDetailRecords.findByCriteria(PHONE_NUMBER_2,
                new Range(DateTime.now().minusDays(1), DateTime.now().plusDays(1)), null, null, null, null, null));
        for (CallDetailRecord cdr : cdrs) {
            allCallDetailRecords.delete((CallDetailRecord) cdr);
        }
    }

}
