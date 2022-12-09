package br.com.oi.sgis.exception;

public class ApplicationNotFoundException extends Exception{
    private static final long serialVersionUID = -8028875422709638510L;

    public ApplicationNotFoundException(String message) {
        super(message);
    }
}
