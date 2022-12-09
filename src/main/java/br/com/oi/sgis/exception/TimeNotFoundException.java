package br.com.oi.sgis.exception;

public class TimeNotFoundException extends Exception{
    private static final long serialVersionUID = 5376213570805141093L;

    public TimeNotFoundException(String message) {
        super(message);
    }
}
