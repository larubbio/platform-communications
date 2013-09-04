package org.motechproject.sms.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.sms.model.Sms;
import org.motechproject.sms.service.SmsSenderService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SmsSenderServiceTest {

    @InjectMocks
    private SmsSenderService smsSender = new SmsSenderServiceImpl();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldSendCriticalNotification() throws Exception {
        Sms sms = new Sms("from", "to", "text");

        smsSender.send(sms);
    }

}
