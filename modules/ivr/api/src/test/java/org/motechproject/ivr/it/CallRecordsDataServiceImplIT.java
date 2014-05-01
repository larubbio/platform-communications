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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.inject.Inject;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class CallRecordsDataServiceImplIT extends BasePaxIT {
    private static final String PHONE_NUMBER = "232";

    @Inject
    IVRDataService calllogSearchService;

    @Inject
    CallDetailRecordService repository;

    @Before
    public void setUp() throws Exception {
        final CallDetailRecord log = new CallDetailRecord("a", PHONE_NUMBER);
        log.setAnswerDate(DateUtil.now().toDate());
        log.setStartDate(DateUtil.now());
        log.setEndDate(DateUtil.now());
        log.setDuration(34);
        log.setCallDirection(CallDirection.INBOUND);
        log.setDisposition(CallDisposition.UNKNOWN);
        repository.create(log);
        final CallDetailRecord b = repository.create(new CallDetailRecord("b", PHONE_NUMBER + "23"));
        b.setDisposition(CallDisposition.ANSWERED);
        b.setCallDirection(CallDirection.OUTBOUND);
        b.setDuration(324);
        repository.update(b);
    }

    @Test
    public void shouldSearchCalllogByPhoneNumber() throws Exception {
        final CallRecordSearchParameters searchParameters = new CallRecordSearchParameters();
        searchParameters.setPhoneNumber(PHONE_NUMBER);
        final List<CallDetailRecord> calllogs = calllogSearchService.search(searchParameters);
        assertEquals(PHONE_NUMBER, calllogs.get(0).getPhoneNumber());
    }

    @Test
    public void shouldReturnAllCalllogs() throws Exception {
        final CallRecordSearchParameters searchParameters = new CallRecordSearchParameters();
        final List<CallDetailRecord> calllogs = calllogSearchService.search(searchParameters);
        assertTrue(calllogs.size() >= 2);
    }

    @After
    public void tearDown() {
        repository.delete(repository.findByCallId("a").get(0));
        repository.delete(repository.findByCallId("b").get(0));
    }
}
