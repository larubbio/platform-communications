package org.motechproject.sms.json;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.motechproject.sms.model.OutgoingSms;

import java.io.IOException;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.motechproject.sms.constants.SendSmsConstants.RECIPIENTS;
import static org.motechproject.sms.constants.SendSmsConstants.MESSAGE;
import static org.motechproject.sms.constants.SendSmsConstants.DELIVERY_TIME;

public class OutgoingSmsDeserializerTest {
    private static final String TEST_FROM = "from@from.com";
    private static final String TEST_TO = "to@to.com";
    private static final String TEST_TEXT = "message";

/*
    private OutgoingSmsDeserializer deserializer = new OutgoingSmsDeserializer();

    @Test
    public void shouldDeserializeJsonToSmsObject() throws Exception {
        assertThat(
                deserializer.deserialize(
                        getJsonParser(TEST_FROM, TEST_TO, TEST_TEXT), null
                ),
                equalTo(new OutgoingSms(TEST_FROM, TEST_TO, TEST_TEXT))
        );
    }

    @Test(expected = JsonMappingException.class)
    public void shouldThrowExceptionWhenFromAddressFieldIsBlank() throws Exception {
        deserializer.deserialize(
                getJsonParser(null, TEST_TO, TEST_TEXT), null
        );
    }

    @Test(expected = JsonMappingException.class)
    public void shouldThrowExceptionWhenToAddressFieldIsBlank() throws Exception {
        deserializer.deserialize(
                getJsonParser(TEST_FROM, null, TEST_TEXT), null
        );
    }

    @Test(expected = JsonMappingException.class)
    public void shouldThrowExceptionWhenTextFieldIsBlank() throws Exception {
        deserializer.deserialize(
                getJsonParser(TEST_FROM, TEST_TO, null), null
        );
    }

    private JsonParser getJsonParser(String from, String to, String text) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory jsonFactory = new JsonFactory(mapper);

        ObjectNode json = mapper.createObjectNode();

        if (isNotBlank(from)) {
            json.put(FROM, from);
        }

        if (isNotBlank(to)) {
            json.put(TO, to);
        }

        if (isNotBlank(text)) {
            json.put(MESSAGE, text);
        }

        return jsonFactory.createJsonParser(json.toString());
    }

*/
}
