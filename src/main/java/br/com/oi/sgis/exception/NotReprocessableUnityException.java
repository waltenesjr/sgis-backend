package br.com.oi.sgis.exception;

public class NotReprocessableUnityException extends Exception{
    private static final long serialVersionUID = 4328469819590976532L;

    public NotReprocessableUnityException(String message) {
        super(message);
    }
}
