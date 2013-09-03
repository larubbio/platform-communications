package org.motechproject.sms.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.sms.model.Sms;
import org.motechproject.sms.service.SmsSenderService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.verify;
import static org.motechproject.sms.constants.SendSmsConstants.FROM_ADDRESS;
import static org.motechproject.sms.constants.SendSmsConstants.MESSAGE;
import static org.motechproject.sms.constants.SendSmsConstants.SEND_SMS_SUBJECT;
import static org.motechproject.sms.constants.SendSmsConstants.SUBJECT;
import static org.motechproject.sms.constants.SendSmsConstants.TO_ADDRESS;

public class SendSmsEventHandlerImplTest {

    @InjectMocks
    SendSmsEventHandlerImpl smsEventHandler = new SendSmsEventHandlerImpl();

    @Mock
    SmsSenderService smsSenderService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIfThereIsHandlerMethodForSendSmsEvent() throws NoSuchMethodException {
        Method handleMethod = smsEventHandler.getClass().getDeclaredMethod("handle", new Class[]{MotechEvent.class});
        assertTrue("MotechListener annotation missing", handleMethod.isAnnotationPresent(MotechListener.class));
        MotechListener annotation = handleMethod.getAnnotation(MotechListener.class);
        assertArrayEquals(new String[]{SEND_SMS_SUBJECT}, annotation.subjects());
    }
/*
    @Test
    public void testIfSmsSenderServiceIsCalledWithEventValues(){

        String from = "testfromaddress";
        String to = "testtoaddress";
        String message = "test message";
        String subject = "test subject";

        Map<String, Object> values = new HashMap<>();
        values.put(FROM_ADDRESS, from);
        values.put(TO_ADDRESS, to);
        values.put(MESSAGE, message);
        values.put(SUBJECT, subject);

        smsEventHandler.handle(new MotechEvent(SEND_SMS_SUBJECT, values));
        ArgumentCaptor<Sms> captor = ArgumentCaptor.forClass(Sms.class);
        verify(smsSenderService).send(captor.capture());

        assertEquals(captor.getValue().getFromAddress(), from);
        assertEquals(captor.getValue().getToAddress(), to);
        assertEquals(captor.getValue().getSubject(), subject);
        assertEquals(captor.getValue().getMessage(), message);
    }
*/
}
