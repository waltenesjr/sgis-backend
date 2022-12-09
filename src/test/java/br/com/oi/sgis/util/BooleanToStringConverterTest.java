package br.com.oi.sgis.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BooleanToStringConverterTest {

    @InjectMocks
    private BooleanToStringConverter booleanToStringConverter;

    @Test
    void shouldConvertBooleanToString(){
        String s = booleanToStringConverter.convertToDatabaseColumn(true);
        String n = booleanToStringConverter.convertToDatabaseColumn(false);

        Assertions.assertEquals("S",s);
        Assertions.assertEquals("N",n);
    }

    @Test
    void shouldConvertStringToBoolean(){
        boolean t = booleanToStringConverter.convertToEntityAttribute("S");
        boolean f = booleanToStringConverter.convertToEntityAttribute("N");

        Assertions.assertTrue(t);
        Assertions.assertFalse(f);
    }

}