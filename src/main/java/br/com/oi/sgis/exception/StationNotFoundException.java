package br.com.oi.sgis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StationNotFoundException extends Exception{
    public StationNotFoundException(String msg) {
        super(msg);
    }
}
