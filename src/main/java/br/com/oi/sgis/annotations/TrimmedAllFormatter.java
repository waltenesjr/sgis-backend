package br.com.oi.sgis.annotations;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class TrimmedAllFormatter extends StringDeserializer{


    private static final long serialVersionUID = -901470527931366008L;

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = super.deserialize(p, ctxt);
        return value != null ? value.trim() : null;
    }
}
