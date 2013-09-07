package org.motechproject.sms.service;


import org.joda.time.DateTime;
import org.motechproject.sms.model.OutgoingSms;

import java.util.List;


public interface SmsSenderService {

    //TODO: remove the 'throws', it's just for testing now
    void send(OutgoingSms message) throws Exception;
    void send(List<String> recipients, String message, DateTime deliveryTime);
    void send(String recipient, String message, DateTime deliveryTime);
}
