package org.motechproject.sms.event;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.sms.constants.SendSmsEventConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//todo: see if this shouldn't inherit from MotechEvent instead...

public class SendSmsEvent {

    private MotechEvent event;

    public SendSmsEvent(String config, List<String> recipients, String message) {
        Map<String, Object> params = new HashMap<>();
        params.put(SendSmsEventConstants.CONFIG, config);
        params.put(SendSmsEventConstants.RECIPIENTS, recipients);
        params.put(SendSmsEventConstants.MESSAGE, message);
        event = new MotechEvent(SendSmsEventConstants.SEND_SMS, params);
    }

    public SendSmsEvent(String config, List<String> recipients, String message, DateTime deliveryTime) {
        Map<String, Object> params = new HashMap<>();
        params.put(SendSmsEventConstants.CONFIG, config);
        params.put(SendSmsEventConstants.RECIPIENTS, recipients);
        params.put(SendSmsEventConstants.MESSAGE, message);
        params.put(SendSmsEventConstants.DELIVERY_TIME, deliveryTime);
        event = new MotechEvent(SendSmsEventConstants.SEND_SMS, params);
    }

    public SendSmsEvent(List<String> recipients, String message) {
        Map<String, Object> params = new HashMap<>();
        params.put(SendSmsEventConstants.RECIPIENTS, recipients);
        params.put(SendSmsEventConstants.MESSAGE, message);
        event = new MotechEvent(SendSmsEventConstants.SEND_SMS, params);
    }

    public SendSmsEvent(List<String> recipients, String message, DateTime deliveryTime) {
        Map<String, Object> params = new HashMap<>();
        params.put(SendSmsEventConstants.RECIPIENTS, recipients);
        params.put(SendSmsEventConstants.MESSAGE, message);
        params.put(SendSmsEventConstants.DELIVERY_TIME, deliveryTime);
        event = new MotechEvent(SendSmsEventConstants.SEND_SMS, params);
    }

    public MotechEvent toMotechEvent() {
        return event;
    }
}
