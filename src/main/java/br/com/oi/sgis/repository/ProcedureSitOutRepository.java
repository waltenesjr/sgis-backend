package br.com.oi.sgis.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface ProcedureSitOutRepository {

    String interventionSituation(String brNumber);
}
