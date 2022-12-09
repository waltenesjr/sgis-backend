package br.com.oi.sgis.exception;

public class TechnologyNotFoundException extends Exception{
    private static final long serialVersionUID = -8615217887817038903L;

    public TechnologyNotFoundException(String message) {
        super(message);
    }
}
