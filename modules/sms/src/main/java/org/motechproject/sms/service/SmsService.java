package org.motechproject.sms.service;

//todo: implement per-message locale?
//todo: using configs for now, but that's not elegant, right?

public interface SmsService {
    void send(final OutgoingSms message);
}
