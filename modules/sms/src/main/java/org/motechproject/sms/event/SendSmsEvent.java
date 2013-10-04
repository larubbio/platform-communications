package org.motechproject.sms.event;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.sms.constants.SendSmsConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//todo: see if this shouldn't inherit from MotechEvent instead...

public class SendSmsEvent {

    private MotechEvent event;

    public SendSmsEvent(String config, List<String> recipients, String message) {
        Map<String, Object> params = new HashMap<>();
        params.put(SendSmsConstants.CONFIG, config);
        params.put(SendSmsConstants.RECIPIENTS, recipients);
        params.put(SendSmsConstants.MESSAGE, message);
        event = new MotechEvent(SendSmsConstants.SEND_SMS, params);
    }

    public SendSmsEvent(String config, List<String> recipients, String message, DateTime deliveryTime) {
        Map<String, Object> params = new HashMap<>();
        params.put(SendSmsConstants.CONFIG, config);
        params.put(SendSmsConstants.RECIPIENTS, recipients);
        params.put(SendSmsConstants.MESSAGE, message);
        params.put(SendSmsConstants.DELIVERY_TIME, deliveryTime);
        event = new MotechEvent(SendSmsConstants.SEND_SMS, params);
    }

    public SendSmsEvent(List<String> recipients, String message) {
        Map<String, Object> params = new HashMap<>();
        params.put(SendSmsConstants.RECIPIENTS, recipients);
        params.put(SendSmsConstants.MESSAGE, message);
        event = new MotechEvent(SendSmsConstants.SEND_SMS, params);
    }

    public SendSmsEvent(List<String> recipients, String message, DateTime deliveryTime) {
        Map<String, Object> params = new HashMap<>();
        params.put(SendSmsConstants.RECIPIENTS, recipients);
        params.put(SendSmsConstants.MESSAGE, message);
        params.put(SendSmsConstants.DELIVERY_TIME, deliveryTime);
        event = new MotechEvent(SendSmsConstants.SEND_SMS, params);
    }

    public MotechEvent toMotechEvent() {
        return event;
    }
}
