package org.motechproject.sms.model;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;


public class ConfigsDeserializer extends JsonDeserializer<Configs> {


    @Override
    public Configs deserialize(JsonParser jsonParser,
                            DeserializationContext deserializationContext) throws IOException {

        //ObjectMapper mapper = new ObjectMapper();
        //Configs configs = mapper.readValue(jsonParser, Configs.class);

        return null;
    }
}
