package br.com.oi.sgis.exception;

public class BoxTypeNotFoundException extends Exception {
    private static final long serialVersionUID = 5488256282736070162L;

    public BoxTypeNotFoundException(String message) {
        super(message);
    }
}
