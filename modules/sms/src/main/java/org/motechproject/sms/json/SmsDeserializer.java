package org.motechproject.sms.json;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.motechproject.sms.model.Sms;

import java.io.IOException;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.sms.constants.SendSmsConstants.FROM_ADDRESS;
import static org.motechproject.sms.constants.SendSmsConstants.MESSAGE;
import static org.motechproject.sms.constants.SendSmsConstants.SUBJECT;
import static org.motechproject.sms.constants.SendSmsConstants.TO_ADDRESS;

public class SmsDeserializer extends JsonDeserializer<Sms> {
    private JsonNode jsonNode;

    @Override
    public Sms deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        jsonNode = jsonParser.readValueAsTree();

        return new Sms(
                getValue(FROM_ADDRESS), getValue(TO_ADDRESS),
                getValue(SUBJECT), getValue(MESSAGE)
        );
    }

    private String getValue(String key) throws JsonMappingException {
        String value = null;

        if (jsonNode.has(key)) {
            value = jsonNode.get(key).getTextValue();
        }

        if (isBlank(value)) {
            throw new JsonMappingException(String.format("Property %s is required", key));
        }

        return value;
    }
}
