package br.com.oi.sgis.repository.impl;


import br.com.oi.sgis.dto.InstallationReasonDTO;
import br.com.oi.sgis.dto.ItensInstallByStealReasonCriteriaDTO;
import br.com.oi.sgis.dto.ItensInstallByStealReasonDTO;
import br.com.oi.sgis.repository.ItensByStealReasonRepository;
import br.com.oi.sgis.util.MessageUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class ItensByStealReasonRepositoryImpl implements ItensByStealReasonRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private StringBuilder sql;

    private ItensInstallByStealReasonCriteriaDTO criteriaDTO;

    @Override
    public List<ItensInstallByStealReasonDTO> findByParamsSteal(ItensInstallByStealReasonCriteriaDTO criteriaDTO) {
        this.criteriaDTO = criteriaDTO;


        sql = new StringBuilder("select new br.com.oi.sgis.dto.ItensInstallByStealReasonDTO(u.situationDateChange, u.instalationReason," +
                        " u.id, ea.description, c.tradeName, t.id, t.technicianName, u.boNumber, u.accountantCompany)" +
                        " from Unity u inner join u.technician t left join u.unityCode ea left join ea.company c where 1 = 1");

        addCriterias();
        sql.append(" order by u.situationDateChange, u.instalationReason asc");
        try {
            TypedQuery<ItensInstallByStealReasonDTO> query = entityManager.createQuery(sql.toString(), ItensInstallByStealReasonDTO.class);
            return query.getResultList();
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ERROR_QUERY_REPORT.getDescription());
        }
    }

    private void addCriterias() {
        responsibleLike();
        periodBetween();
        reasonIn();
    }

    private void reasonIn() {
        List<String> reasonSteal = criteriaDTO.getInstallationReason().stream().map(InstallationReasonDTO::getCod).collect(toList());
        if(reasonSteal!=null && !reasonSteal.isEmpty())
            sql.append(" and ( u.instalationReason in ( ")
                    .append(reasonSteal.stream().collect(Collectors.joining ("','", "'", "'")))
                    .append(" ) )");
    }

    private void periodBetween() {
        conditionalDateBetween(criteriaDTO.getInitialPeriod(), criteriaDTO.getFinalPeriod(), "u.situationDateChange");
    }

    private void responsibleLike() {
        if(criteriaDTO.getResponsibleCode()!=null)
            conditionalLike(criteriaDTO.getResponsibleCode(), "and","u.deposit");
    }

    private void conditionalLike(String value, String conditional, String attribute) {
        sql.append(conditional).append(" (upper(").append(attribute).append(") like '%").append(value.toUpperCase(Locale.ROOT)).append("%') ");
    }

    private void conditionalDateBetween(LocalDateTime initialDate, LocalDateTime finalDate, String value){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if(initialDate!=null && finalDate!=null)
            sql.append(" and (").append(value).append(" between TO_DATE('").append(initialDate.format(formatter))
                    .append("','YYYY-MM-DD') and TO_DATE('").append(finalDate.format(formatter)).append("','YYYY-MM-DD')) ");
    }

}
