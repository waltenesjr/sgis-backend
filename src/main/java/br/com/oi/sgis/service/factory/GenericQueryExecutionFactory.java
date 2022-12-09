package br.com.oi.sgis.service.factory;

import br.com.oi.sgis.dto.GenericQueryDTO;

public interface GenericQueryExecutionFactory {
    String createSqlQuery(GenericQueryDTO genericQueryDTO);
}
