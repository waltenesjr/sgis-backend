package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.repository.ProcedureSitOutRepository;

import javax.persistence.*;

public class ProcedureSitOutRepositoryImpl implements ProcedureSitOutRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public String interventionSituation(String brNumber) {
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("P_REDISIT_BILHETE");
        storedProcedureQuery.registerStoredProcedureParameter("numbr", String.class, ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("sit_final", String.class, ParameterMode.OUT);
        storedProcedureQuery.registerStoredProcedureParameter("contrato_out", String.class, ParameterMode.OUT);
        storedProcedureQuery.registerStoredProcedureParameter("empresa_out", String.class, ParameterMode.OUT);
        storedProcedureQuery.registerStoredProcedureParameter("tecnico_out", String.class, ParameterMode.OUT);
        storedProcedureQuery.setParameter("numbr", brNumber);
        storedProcedureQuery.execute();

        String sit = String.valueOf(storedProcedureQuery.getOutputParameterValue("sit_final"));
        return sit;
    }
}
