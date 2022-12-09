package br.com.oi.sgis.repository;

import java.util.List;

public interface GenericQueryCustomRepository {
    List<Object> executeQuery(String query);
}
