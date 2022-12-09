package br.com.oi.sgis.util;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;

public class CpfCnpjValidator {

    private CpfCnpjValidator(){}

    public static boolean isValidCPF(String cpf){
        CPFValidator validator = new CPFValidator();
        try{
            validator.assertValid(cpf);
            return true;
        }catch (InvalidStateException e){
            throw new IllegalArgumentException("CPF inválido");
        }
    }

    public static boolean isValidCNPJ(String cnpj){
        CNPJValidator validator = new CNPJValidator();
        try{
            validator.assertValid(cnpj);
            return true;
        }catch (InvalidStateException e){
            throw new IllegalArgumentException("CNPJ inválido");
        }
    }
}
