package br.com.oi.sgis.exception;

public class CompanyNotFoundException extends Exception{
    private static final long serialVersionUID = -2401376085022276120L;

    public CompanyNotFoundException(String message) {
        super(message);
    }
}
