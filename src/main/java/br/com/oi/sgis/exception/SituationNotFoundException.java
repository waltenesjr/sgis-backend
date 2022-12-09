package br.com.oi.sgis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)


public class SituationNotFoundException extends Exception{
    public SituationNotFoundException(String msg) {
        super(msg);
    }

}
