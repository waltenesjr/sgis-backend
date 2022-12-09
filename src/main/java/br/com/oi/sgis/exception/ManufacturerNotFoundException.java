package br.com.oi.sgis.exception;

public class ManufacturerNotFoundException extends Exception {
    private static final long serialVersionUID = -2662032791248430897L;

    public ManufacturerNotFoundException(String message) {
        super(message);
    }
}
