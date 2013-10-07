package org.motechproject.sms.service;

import org.motechproject.sms.settings.OutgoingSms;

//todo: implement per-message locale?
//todo: using configs for now, but that's not elegant, right?

public interface SmsService {
    void send(final OutgoingSms message);
}
