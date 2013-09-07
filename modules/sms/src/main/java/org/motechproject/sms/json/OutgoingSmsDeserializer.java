package org.motechproject.sms.json;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.joda.time.DateTime;
import org.motechproject.sms.model.OutgoingSms;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.sms.constants.SendSmsConstants.RECIPIENTS;
import static org.motechproject.sms.constants.SendSmsConstants.MESSAGE;
import static org.motechproject.sms.constants.SendSmsConstants.DELIVERY_TIME;

public class OutgoingSmsDeserializer extends JsonDeserializer<OutgoingSms> {
    private JsonNode jsonNode;

    @Override
    public OutgoingSms deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        jsonNode = jsonParser.readValueAsTree();

        // Split CSV recipients into a string list
        List<String> recipients = Arrays.asList(getStringValue(RECIPIENTS).split("\\s*,\\s*"));

        return new OutgoingSms(recipients, getStringValue(MESSAGE), getDateTimeValueOrNull(DELIVERY_TIME));
    }

    private String getStringValue(String key) throws JsonMappingException {
        String value = null;

        if (jsonNode.has(key)) {
            value = jsonNode.get(key).getTextValue();
        }

        if (isBlank(value)) {
            throw new JsonMappingException(String.format("Property %s is required", key));
        }

        return value;
    }

    private DateTime getDateTimeValueOrNull(String key) {

        DateTime value = null;

        if (jsonNode.has(key)) {
            String s = jsonNode.get(key).getTextValue();

            if (!isBlank(s)) {
                //TODO: This throws IllegalArgumentException, what do I want to do about it?
                value = DateTime.parse(s);
            }

        }

        return value;
    }
}
