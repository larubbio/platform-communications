package org.motechproject.sms.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.sms.service.SmsSenderService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;

import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.verify;
import static org.motechproject.sms.constants.SendSmsConstants.SEND_SMS;

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
        assertArrayEquals(new String[]{SEND_SMS}, annotation.subjects());
    }
/*
    @Test
    public void testIfSmsSenderServiceIsCalledWithEventValues(){

        String from = "testfromaddress";
        String to = "testtoaddress";
        String message = "test message";
        String subject = "test subject";

        Map<String, Object> values = new HashMap<>();
        values.put(FROM, from);
        values.put(TO, to);
        values.put(MESSAGE, message);
        values.put(SUBJECT, subject);

        smsEventHandler.handle(new MotechEvent(SEND_SMS, values));
        ArgumentCaptor<Sms> captor = ArgumentCaptor.forClass(Sms.class);
        verify(smsSenderService).send(captor.capture());

        assertEquals(captor.getValue().getFromAddress(), from);
        assertEquals(captor.getValue().getToAddress(), to);
        assertEquals(captor.getValue().getSubject(), subject);
        assertEquals(captor.getValue().getMessage(), message);
    }
*/
}
