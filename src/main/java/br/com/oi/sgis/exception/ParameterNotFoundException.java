package br.com.oi.sgis.exception;

public class ParameterNotFoundException extends Exception {
    private static final long serialVersionUID = 1391132898174936026L;

    public ParameterNotFoundException(String message) {
        super(message);
    }
}
