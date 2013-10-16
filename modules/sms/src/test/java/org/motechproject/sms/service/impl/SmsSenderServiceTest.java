package org.motechproject.sms.service.impl;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.http.SmsServiceImpl;
import org.motechproject.sms.service.SmsAuditService;
import org.motechproject.sms.service.SmsService;
import org.motechproject.sms.templates.TemplateReader;

import static org.mockito.MockitoAnnotations.initMocks;

public class SmsSenderServiceTest {

    @Mock
    private SettingsFacade settings;
    @Mock
    private EventRelay eventRelay;
    @Mock
    MotechSchedulerService schedulerService;
    @Mock
    TemplateReader templateReader;
    @Mock
    SmsAuditService smsAuditService;

    @InjectMocks
    private SmsService smsSender = new SmsServiceImpl(settings, eventRelay, schedulerService, templateReader,
        smsAuditService);

    @Before
    public void setUp() {
        initMocks(this);
    }
/*
    @Test
    public void shouldSendCriticalNotification() throws Exception {
        OutgoingSms outgoingSms = new OutgoingSms(Arrays.asList(new String[]{"+12065551212"}), "sample message");

        smsSender.send(outgoingSms);
    }
*/
}
