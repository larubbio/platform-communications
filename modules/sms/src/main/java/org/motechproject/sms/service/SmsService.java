package org.motechproject.sms.service;


import org.motechproject.sms.model.OutgoingSms;


public interface SmsService {
    void send(final OutgoingSms message);
}
