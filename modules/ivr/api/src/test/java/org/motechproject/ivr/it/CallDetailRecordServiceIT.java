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
import org.ops4j.pax.exam.util.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger(CallDetailRecordServiceIT.class);

    @Inject @Filter(timeout = 360000)
    CallDetailRecordService callDetailRecordService;

    @Before
    public void setUp() throws Exception {
        LOG.info("********** setUp() in  **********");
        final CallDetailRecord callDetailRecordA = callDetailRecordService.create(
                new CallDetailRecord(CALL_ID_A, PHONE_NUMBER));
        final CallDetailRecord callDetailRecordB = callDetailRecordService.create(
                new CallDetailRecord(CALL_ID_B, PHONE_NUMBER + "23"));
        callDetailRecordB.setDisposition(CallDisposition.ANSWERED);
        callDetailRecordB.setCallDirection(CallDirection.OUTBOUND);
        callDetailRecordService.update(callDetailRecordB);
        LOG.info("********** setUp() out **********");
    }

    @Test
    public void shouldSearchByCallId() throws Exception {
        LOG.info("********** shouldSearchByCallId() in  **********");
        final List<CallDetailRecord> callDetailRecords = callDetailRecordService.findByCallId(CALL_ID_A);
        assertEquals(CALL_ID_A, callDetailRecords.get(0).getCallId());
        LOG.info("********** shouldSearchByCallId() out **********");
    }

    @After
    public void tearDown() {
        LOG.info("********** tearDown() in  **********");
        //TODO: erase the records we created in setUp()
        // but right now calling .delete() brings up the weird error below:
        // Object with id "5" is managed by a different persistence manager
        // See https://applab.atlassian.net/browse/MOTECH-1008
        /*
        CallDetailRecord callDetailRecordA = callDetailRecordService.findByCallId(CALL_ID_A).get(0);
        CallDetailRecord callDetailRecordB = callDetailRecordService.findByCallId(CALL_ID_B).get(0);
        callDetailRecordService.delete(callDetailRecordA);
        callDetailRecordService.delete(callDetailRecordB);
        */
        LOG.info("********** tearDown() out **********");
    }
}
