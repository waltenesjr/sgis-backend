package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.ItemBySitReportCriteriaDTO;
import br.com.oi.sgis.dto.ItemBySituationViewDTO;
import br.com.oi.sgis.repository.SituationItemRepository;
import br.com.oi.sgis.util.MessageUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class SituationItemRepositoryImpl implements SituationItemRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private StringBuilder sql;

    private ItemBySitReportCriteriaDTO criteriaReportDTO;


    @Override
    public List<ItemBySituationViewDTO> findSituationByParams(ItemBySitReportCriteriaDTO criteriaDTO) {
        criteriaReportDTO = criteriaDTO;

         sql =  new StringBuilder("select new br.com.oi.sgis.dto.ItemBySituationViewDTO(" +
                 "sum(case when u.situationCode = 'TRN' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'DIS' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'EMP' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'TDR' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'EMU' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'TRR' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'BXP' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'TRP' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'TRE' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'REP' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'BXU' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'BXE' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'DVR' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'TRD' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'USO' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'BXI' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'PRE' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'BXC' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'BXO' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'RES' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'OFE' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'DEF' then 1 else 0 end)," +
                 "sum(case when u.situationCode = 'TDD' then 1 else 0 end), " +
                 "max(concat(ae.id, ' - ',case when ae.mnemonic is not null then ae.mnemonic else '' end, ' - ', ae.description )), r.id, 0L) " +
                 "from Unity u left join u.unityCode ae left join u.responsible r   where 1=1 ");

         addCriterias();
        sql.append(" group by r.id, ae.id");
        sql.append(" order by r.id, ae.id");
        try {
            TypedQuery<ItemBySituationViewDTO> query = entityManager.createQuery(sql.toString(), ItemBySituationViewDTO.class);
            return  query.getResultList();
        }catch (Exception e ){
            throw new IllegalArgumentException(MessageUtils.ERROR_QUERY_REPORT.getDescription());
        }
    }

    private void addCriterias() {
        departmentLike();
        regDateBetween();
        movDateBetween();
    }

    private void regDateBetween() {
        conditionalDateBetween(criteriaReportDTO.getInitialRegDate(), criteriaReportDTO.getFinalRegDate(), "u.registerDate");
    }

    private void movDateBetween() {
        conditionalDateBetween(criteriaReportDTO.getInitialMovDate(), criteriaReportDTO.getFinalMovDate(), "u.situationDateChange");
    }

    private void departmentLike() {
        if(criteriaReportDTO.getDepartment()!=null) {
            conditionalLike(criteriaReportDTO.getDepartment(), "and","r.id");
            conditionalLike(criteriaReportDTO.getDepartment(), "or","r.directorship");
        }
    }
    private void conditionalLike(String value, String conditional, String attribute) {
            sql.append(conditional).append(" (upper(").append(attribute).append(") like '%").append(value.toUpperCase(Locale.ROOT)).append("%') ");
    }

    private void conditionalDateBetween(LocalDateTime initialDate, LocalDateTime finalDate, String value){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if(initialDate!=null && finalDate!=null)
            sql.append(" and (").append(value).append(" between '").append(initialDate.format(formatter))
                    .append("' and '").append(finalDate.format(formatter)).append("') ");
    }



}
