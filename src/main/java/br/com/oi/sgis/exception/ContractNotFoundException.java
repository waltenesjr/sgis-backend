package br.com.oi.sgis.exception;


public class ContractNotFoundException extends Exception {
    private static final long serialVersionUID = -826438399634231526L;

    public ContractNotFoundException(String message) {
        super(message);
    }
}
