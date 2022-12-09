package br.com.oi.sgis.exception;

public class CompanyModelNotFoundException extends Exception{
    private static final long serialVersionUID = -6608095159604223835L;

    public CompanyModelNotFoundException(String message) {
        super(message);
    }
}
