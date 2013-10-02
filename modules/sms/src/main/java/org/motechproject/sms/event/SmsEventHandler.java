package org.motechproject.sms.event;

import org.motechproject.event.MotechEvent;
import org.motechproject.sms.SmsDeliveryFailureException;

public interface SmsEventHandler {
    void handle(MotechEvent event) throws SmsDeliveryFailureException;
}
