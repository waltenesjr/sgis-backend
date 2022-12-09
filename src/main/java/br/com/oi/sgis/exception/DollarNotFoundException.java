package br.com.oi.sgis.exception;

public class DollarNotFoundException extends Exception{
    private static final long serialVersionUID = 2352064798190906957L;

    public DollarNotFoundException(String message) {
        super(message);
    }
}
