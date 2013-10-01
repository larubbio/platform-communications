package org.motechproject.sms.web;

import static org.mockito.Mockito.verify;

public class SendSmsControllerTest {
/*
    @Mock
    private SmsService senderService;

    private SendSmsController sendSmsController;

    private MockMvc controller;

    private OutgoingSms outgoingSms = new OutgoingSms("from", "to", "message");

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        sendSmsController = new SendSmsController(senderService);
        controller = MockMvcBuilders.standaloneSetup(sendSmsController).build();
    }

    @Test
    public void shouldSendSms() throws Exception {
        sendSmsController.sendSms(outgoingSms);

        verify(senderService).send(outgoingSms);
    }

    @Test
    public void shouldExecuteSendSmsRequest() throws Exception {
        controller.perform(
                post("/send").body(convertSmsToJson()).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.SC_OK)
        );
    }

    @Test
    public void shouldHandleExceptionDuringExecutionSendSmsRequest() throws Exception {
        String message = "There are problems with sending sms";
        doThrow(new IllegalStateException(message)).when(senderService).send(outgoingSms);

        controller.perform(
                post("/send").body(convertSmsToJson()).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.SC_NOT_FOUND)
        ).andExpect(
                content().string(message)
        );
    }

    private byte[] convertSmsToJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();

        json.put(FROM, outgoingSms.getFromAddress());
        json.put(TO, outgoingSms.getToAddress());
        json.put(MESSAGE, outgoingSms.getMessage());

        return json.toString().getBytes();
    }
*/
}
