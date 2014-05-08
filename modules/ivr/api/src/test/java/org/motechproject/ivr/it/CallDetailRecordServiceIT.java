package org.motechproject.ivr.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.domain.CallDirection;
import org.motechproject.ivr.domain.CallDisposition;
import org.motechproject.ivr.service.CallDetailRecordService;
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
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class CallDetailRecordServiceIT extends BasePaxIT {
    private static final String CALL_ID_A = "call-id-a";
    private static final String CALL_ID_B = "call-id-b";
    private static final String PHONE_NUMBER = "232";

    @Inject
    CallDetailRecordService callDetailRecordService;

    @Before
    public void setUp() throws Exception {
        final CallDetailRecord callDetailRecordA = callDetailRecordService.create(
                new CallDetailRecord(CALL_ID_A, PHONE_NUMBER));
        final CallDetailRecord callDetailRecordB = callDetailRecordService.create(
                new CallDetailRecord(CALL_ID_B, PHONE_NUMBER + "23"));
        callDetailRecordB.setDisposition(CallDisposition.ANSWERED);
        callDetailRecordB.setCallDirection(CallDirection.OUTBOUND);
        callDetailRecordService.update(callDetailRecordB);
    }

    @Test
    public void shouldSearchByCallId() throws Exception {
        final List<CallDetailRecord> callDetailRecords = callDetailRecordService.findByCallId(CALL_ID_A);
        assertEquals(CALL_ID_A, callDetailRecords.get(0).getCallId());
    }

    @After
    public void tearDown() {
        CallDetailRecord callDetailRecordA = callDetailRecordService.findByCallId(CALL_ID_A).get(0);
        CallDetailRecord callDetailRecordB = callDetailRecordService.findByCallId(CALL_ID_B).get(0);
        callDetailRecordService.delete(callDetailRecordA);
        callDetailRecordService.delete(callDetailRecordB);
    }
}
