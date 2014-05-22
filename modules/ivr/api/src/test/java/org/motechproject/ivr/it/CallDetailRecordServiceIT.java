package org.motechproject.ivr.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.domain.CallDirection;
import org.motechproject.ivr.domain.CallDisposition;
import org.motechproject.ivr.domain.CallEventLog;
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
import org.ops4j.pax.exam.util.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class CallDetailRecordServiceIT extends BasePaxIT {
    private static final String CALL_ID_A = "call-id-a";
    private static final String CALL_ID_B = "call-id-b";
    private static final String PHONE_NUMBER_A = "555-1212";
    private static final String PHONE_NUMBER_B = "555-1313";
    private static final Logger LOG = LoggerFactory.getLogger(CallDetailRecordServiceIT.class);

    @Inject
    CallDetailRecordService callDetailRecordService;

    @Before
    public void setUp() throws Exception {
        LOG.info("********** setUp() in  **********");
        callDetailRecordService.deleteAll();
        LOG.info("********** setUp() out **********");
    }

    @Test
    public void shouldSearchByCallId() throws Exception {
        LOG.info("********** shouldSearchByCallId() in  **********");
        CallDetailRecord c = callDetailRecordService.create(new CallDetailRecord(CALL_ID_A, PHONE_NUMBER_A));
        c.getCustomProperties().put("foo", "bar");
        c.getCallEvents().add(new CallEventLog("fubar"));
        c.setBar("foowee!");
        callDetailRecordService.update(c);
        callDetailRecordService.create(new CallDetailRecord(CALL_ID_B, PHONE_NUMBER_B));
        final List<CallDetailRecord> callDetailRecords = callDetailRecordService.findByCallId(CALL_ID_A);
        assertEquals(CALL_ID_A, callDetailRecords.get(0).getCallId());
        LOG.info("********** shouldSearchByCallId() out **********");
    }

    @Test
    public void shouldCountAccurately() throws Exception {
        LOG.info("********** shouldCouldAccurately() in  **********");
        callDetailRecordService.create(new CallDetailRecord(CALL_ID_A, PHONE_NUMBER_A));
        callDetailRecordService.create(new CallDetailRecord(CALL_ID_B, PHONE_NUMBER_B));
        callDetailRecordService.create(new CallDetailRecord(CALL_ID_A, PHONE_NUMBER_A));
        callDetailRecordService.create(new CallDetailRecord(CALL_ID_B, PHONE_NUMBER_B));
        callDetailRecordService.create(new CallDetailRecord(CALL_ID_A, PHONE_NUMBER_A));
        callDetailRecordService.create(new CallDetailRecord(CALL_ID_B, PHONE_NUMBER_B));
        long count = callDetailRecordService.countByCallId(CALL_ID_A);
        assertEquals(3, count);
        LOG.info("********** shouldCouldAccurately() out **********");
    }

    @After
    public void tearDown() {
        LOG.info("********** tearDown() in  **********");
        LOG.info("********** tearDown() out **********");
    }
}
