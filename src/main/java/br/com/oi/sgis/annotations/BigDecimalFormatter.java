package br.com.oi.sgis.annotations;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.math.BigDecimal;

@JsonComponent
public class BigDecimalFormatter extends NumberDeserializers.BigDecimalDeserializer {
    private static final long serialVersionUID = 3848573783353470431L;

    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            return super.deserialize(p, ctxt);
        }catch (InvalidFormatException e){
            String valueString = p.getText().replace(",", ".").replaceAll("[^\\d.]", "").trim();
            return new BigDecimal(valueString);
        }
    }
}
