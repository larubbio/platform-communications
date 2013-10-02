package org.motechproject.sms.event;

/*
import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

import static org.motechproject.commons.date.util.DateUtil.now;


@Component
public class SmsSendHandler implements SmsEventHandler {
    private SmsHttpService smsHttpService;
    private SmsAuditService smsAuditService;

    private Random random = new Random();


    @Autowired
    public SmsSendHandler(SmsHttpService smsHttpService, SmsAuditService smsAuditService) {
        this.smsHttpService = smsHttpService;
        this.smsAuditService = smsAuditService;
    }

    @Override
    @MotechListener(subjects = {EventSubjects.SEND_SMS, EventSubjects.SEND_SMSDT})
    public void handle(MotechEvent event) throws SmsDeliveryFailureException {
        List<String> recipients = (List<String>) event.getParameters().get(RECIPIENTS);
        String message = (String) event.getParameters().get(MESSAGE);
        DateTime deliveryTime = (DateTime) event.getParameters().get(DELIVERY_TIME);

        Object value = event.getParameters().get(FAILURE_COUNT);
        Integer failureCount = value == null ? 0 : (Integer) value;

        for (String recipient : recipients) {
            smsAuditService.log(new SmsRecord(
                    OUTBOUND, recipient, message, now(), PENDING,
                    Integer.toString(Math.abs(random.nextInt()))
            ));
        }

        if (deliveryTime == null) {
            smsHttpService.sendSms(recipients, message, failureCount);
        } else {
            smsHttpService.sendSms(recipients, message, failureCount, deliveryTime);
        }

    }
}
*/