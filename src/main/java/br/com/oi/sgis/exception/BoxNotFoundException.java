package br.com.oi.sgis.exception;

public class BoxNotFoundException extends Exception{
    private static final long serialVersionUID = 3761025389826010442L;

    public BoxNotFoundException(String message) {
        super(message);
    }
}
