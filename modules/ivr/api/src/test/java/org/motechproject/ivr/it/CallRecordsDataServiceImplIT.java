package org.motechproject.ivr.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.domain.CallDirection;
import org.motechproject.ivr.domain.CallDisposition;
import org.motechproject.ivr.domain.CallRecordSearchParameters;
import org.motechproject.ivr.service.CallDetailRecordService;
import org.motechproject.ivr.service.IVRDataService;
import org.motechproject.testing.osgi.BasePaxIT;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class CallRecordsDataServiceImplIT extends BasePaxIT {
    private static final String PHONE_NUMBER = "232";

    @Inject
    IVRDataService ivrDataServiceSearchService;

    @Inject
    CallDetailRecordService repository;

    @Before
    public void setUp() throws Exception {
        final CallDetailRecord callDetailRecord = new CallDetailRecord("a", PHONE_NUMBER);
        callDetailRecord.setAnswerDate(DateUtil.now().toDate());
        callDetailRecord.setStartDate(DateUtil.now());
        callDetailRecord.setEndDate(DateUtil.now());
        callDetailRecord.setDuration(34);
        callDetailRecord.setCallDirection(CallDirection.INBOUND);
        callDetailRecord.setDisposition(CallDisposition.UNKNOWN);
        repository.create(callDetailRecord);
        final CallDetailRecord b = repository.create(new CallDetailRecord("b", PHONE_NUMBER + "23"));
        b.setDisposition(CallDisposition.ANSWERED);
        b.setCallDirection(CallDirection.OUTBOUND);
        b.setDuration(324);
        repository.update(b);
    }

    @Test
    public void shouldSearchByPhoneNumber() throws Exception {
        final CallRecordSearchParameters searchParameters = new CallRecordSearchParameters();
        searchParameters.setPhoneNumber(PHONE_NUMBER);
        final List<CallDetailRecord> callDetailRecords = ivrDataServiceSearchService.search(searchParameters);
        assertEquals(PHONE_NUMBER, callDetailRecords.get(0).getPhoneNumber());
    }

    @Test
    public void shouldReturnAllCallRecords() throws Exception {
        final CallRecordSearchParameters searchParameters = new CallRecordSearchParameters();
        final List<CallDetailRecord> callDetailRecords = ivrDataServiceSearchService.search(searchParameters);
        assertTrue(callDetailRecords.size() >= 2);
    }

    //todo: test searching using criteria

    @After
    public void tearDown() {
        repository.delete(repository.findByCallId("a").get(0));
        repository.delete(repository.findByCallId("b").get(0));
    }
}
