package org.motechproject.sms.web;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.sms.model.OutgoingSms;
import org.motechproject.sms.service.SmsSenderService;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.sms.constants.SendSmsConstants.RECIPIENTS;
import static org.motechproject.sms.constants.SendSmsConstants.MESSAGE;
import static org.motechproject.sms.constants.SendSmsConstants.DELIVERY_TIME;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class SendSmsControllerTest {
/*
    @Mock
    private SmsSenderService senderService;

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
