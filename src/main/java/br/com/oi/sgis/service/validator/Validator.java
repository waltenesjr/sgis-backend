package br.com.oi.sgis.service.validator;


public interface Validator<T> {
    void validate(T object);
}
