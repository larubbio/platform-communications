package org.motechproject.sms.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.motechproject.sms.model.OutgoingSms;
import org.motechproject.sms.service.SmsSenderService;

import java.util.Arrays;

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
        OutgoingSms outgoingSms = new OutgoingSms(Arrays.asList(new String[]{"+12065551212"}), "sample message");

        smsSender.send(outgoingSms);
    }

}
