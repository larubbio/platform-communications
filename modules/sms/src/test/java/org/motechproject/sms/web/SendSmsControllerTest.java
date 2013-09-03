package org.motechproject.sms.web;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.sms.model.Sms;
import org.motechproject.sms.service.SmsSenderService;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.sms.constants.SendSmsConstants.FROM_ADDRESS;
import static org.motechproject.sms.constants.SendSmsConstants.MESSAGE;
import static org.motechproject.sms.constants.SendSmsConstants.SUBJECT;
import static org.motechproject.sms.constants.SendSmsConstants.TO_ADDRESS;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class SendSmsControllerTest {
    @Mock
    private SmsSenderService senderService;

    private SendSmsController sendSmsController;

    private MockMvc controller;

    private Sms sms = new Sms("from", "to", "subject", "message");

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        sendSmsController = new SendSmsController(senderService);
        controller = MockMvcBuilders.standaloneSetup(sendSmsController).build();
    }

    @Test
    public void shouldSendSms() throws Exception {
        sendSmsController.sendSms(sms);

        verify(senderService).send(sms);
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
        doThrow(new IllegalStateException(message)).when(senderService).send(sms);

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

        json.put(FROM_ADDRESS, sms.getFromAddress());
        json.put(TO_ADDRESS, sms.getToAddress());
        json.put(SUBJECT, sms.getSubject());
        json.put(MESSAGE, sms.getMessage());

        return json.toString().getBytes();
    }
}
