package br.com.oi.sgis.repository.impl;


import br.com.oi.sgis.repository.GenericQueryCustomRepository;
import br.com.oi.sgis.util.MessageUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class GenericQueryCustomRepositoryImpl implements GenericQueryCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<Object> executeQuery(String sql) {
        try {
            Query query = entityManager.createNativeQuery(sql);
            return query.getResultList();
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ERROR_QUERY_REPORT.getDescription());
        }
    }
}
