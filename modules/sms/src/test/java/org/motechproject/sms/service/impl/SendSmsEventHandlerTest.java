package org.motechproject.sms.service.impl;

public class SendSmsEventHandlerTest {
/*
    @InjectMocks
    SendSmsEventHandler smsEventHandler = new SendSmsEventHandler();

    @Mock
    SmsService smsService;

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
        verify(smsService).send(captor.capture());

        assertEquals(captor.getValue().getFromAddress(), from);
        assertEquals(captor.getValue().getToAddress(), to);
        assertEquals(captor.getValue().getSubject(), subject);
        assertEquals(captor.getValue().getMessage(), message);
    }
*/
}
