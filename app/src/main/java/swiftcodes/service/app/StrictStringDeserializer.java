package swiftcodes.service.app;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

public class StrictStringDeserializer extends StdDeserializer<String> {

    public StrictStringDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!p.getCurrentToken().equals(JsonToken.VALUE_STRING)) {
            //Exception if token is not a string.
            throw JsonMappingException.from(p, "Expected a string value for property, got: " + p.getCurrentToken());

        }
        return p.getText();
    }
}