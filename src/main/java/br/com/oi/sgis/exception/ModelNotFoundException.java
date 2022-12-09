package br.com.oi.sgis.exception;

public class ModelNotFoundException extends Exception {
    private static final long serialVersionUID = 859563661914404786L;

    public ModelNotFoundException(String message) {
        super(message);
    }
}
