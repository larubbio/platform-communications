package org.motechproject.sms.service;

//todo: Implement per-message locale
//todo: You can use configs for now, but that's not elegant. Right?

/**
 * Send an SMS
 */
public interface SmsService {
    void send(final OutgoingSms message);
}
