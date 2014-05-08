package org.motechproject.sms.it;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.sms.audit.DeliveryStatus;
import org.motechproject.sms.audit.SmsAuditService;
import org.motechproject.sms.audit.SmsDirection;
import org.motechproject.sms.audit.SmsRecord;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class SmsAuditServiceIT  extends BasePaxIT {

    @Inject @Filter(timeout=360000)
    private SmsAuditService smsAuditService;

    @Before
    public void setUp() throws Exception {
        smsAuditService.log(new SmsRecord("config", SmsDirection.OUTBOUND, "123", "msg", DateTime.now(),
                DeliveryStatus.DELIVERY_CONFIRMED, null, null, null, null));
    }

    @Test
    public void shouldFindAllRecords() throws Exception {
        List<SmsRecord> smsRecords = smsAuditService.findAllSmsRecords();
        assertTrue(smsRecords.size() >= 1);
    }
}
