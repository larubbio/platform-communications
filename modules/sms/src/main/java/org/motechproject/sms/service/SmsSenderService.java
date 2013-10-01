package org.motechproject.sms.service;


import org.joda.time.DateTime;
import org.motechproject.sms.model.OutgoingSms;

import java.util.List;


public interface SmsSenderService {
    void send(final OutgoingSms message);
    void send(final List<String> recipients, final String message);
    void send(final List<String> recipients, final String message, final DateTime deliveryTime);
    void send(final String config, final List<String> recipients, final String message);
    void send(final String config, final List<String> recipients, final String message, final DateTime deliveryTime);
}
