package br.com.oi.sgis.exception;

public class DefectNotFoundException extends Exception{
    private static final long serialVersionUID = -9080700293319997806L;

    public DefectNotFoundException(String message) {
        super(message);
    }
}
