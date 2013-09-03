package org.motechproject.sms.service;


import org.motechproject.sms.model.Sms;

public interface SmsSenderService {

    void send(Sms message);
}
