package br.com.oi.sgis.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BooleanToStringConverter implements AttributeConverter<Boolean, String> {
    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return (attribute != null && attribute) ? "S" : "N";
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        if(dbData==null)
            return false;

        return dbData.equals("S");
    }
}
