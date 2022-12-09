package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.RegisteredItensCriteriaDTO;
import br.com.oi.sgis.dto.RegisteredItensDTO;
import br.com.oi.sgis.repository.RegisteredItensRepository;
import br.com.oi.sgis.util.MessageUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RegisteredItensRepositoryImpl implements RegisteredItensRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private StringBuilder sql;

    private RegisteredItensCriteriaDTO criteriaDTO;

    @Override
    public List<RegisteredItensDTO> findRegisteredItens(RegisteredItensCriteriaDTO registeredItensCriteriaDTO) {
        this.criteriaDTO = registeredItensCriteriaDTO;

        sql =  new StringBuilder("select  new br.com.oi.sgis.dto.RegisteredItensDTO(u.registerDate," +
                " pt.id, pt.technicianName, (case when (u.sapStatus = '0') then 'Sucesso' when (u.sapStatus = '1') then concat('ERRO - ', u.sapMesage)  " +
                " else 'Pendente processamento SAP' end) as sapStatus, u.id, ea.description, emp.tradeName, u.accountantCompany," +
                " (case when (u.orderNumber is not null and u.fixedNumber is null and u.reservationNumber is null) then 'Nº DO PEDIDO: '" +
                "      when (u.orderNumber is null and u.fixedNumber is not null and u.reservationNumber is null) then 'Nº IMOBILIZADO: '" +
                "     else 'Nº RESERVA: ' end) as numberType, " +
                " (case when (u.orderNumber is not null and u.fixedNumber is null and u.reservationNumber is null) then u.orderNumber " +
                "      when (u.orderNumber is null and u.fixedNumber is not null and u.reservationNumber is null) then u.fixedNumber " +
                "      else u.reservationNumber end) as number ) from Unity u" +
                " left join u.technician pt left join u.unityCode ea left join ea.company emp " +
                " where u.registerReason = 'CUS'");
        addCriterias();
        sql.append(" order by number, numberType, u.registerDate ASC");
        try {
            TypedQuery<RegisteredItensDTO> query = entityManager.createQuery(sql.toString(), RegisteredItensDTO.class);
            return  query.getResultList();
        }catch (Exception e ){
            throw new IllegalArgumentException(MessageUtils.ERROR_QUERY_REPORT.getDescription());
        }
    }

    private void addCriterias() {
        departmentLike();
        regDateBetween();
        numberLike();
    }

    private void numberLike() {
        if(criteriaDTO.getNumber() != null) {
            sql.append(" and u.orderNumber = '").append(criteriaDTO.getNumber()).append("' ");
            sql.append(" or u.fixedNumber = '").append(criteriaDTO.getNumber()).append("' ");
            sql.append(" or u.reservationNumber = '").append(criteriaDTO.getNumber()).append("' ");
        }
    }

    private void departmentLike() {
        equalsOrLike(criteriaDTO.getResponsibleId(), "u.deposit.id");
    }

    private void regDateBetween() {
        conditionalDateBetween(criteriaDTO.getInitialPeriod(), criteriaDTO.getFinalPeriod(), "u.registerDate");
    }

    private void conditionalDateBetween(LocalDateTime initialDate, LocalDateTime finalDate, String value){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if(initialDate!=null && finalDate!=null)
            sql.append(" and (").append(value).append(" between TO_DATE('").append(initialDate.format(formatter))
                    .append("','YYYY-MM-DD') and TO_DATE('").append(finalDate.format(formatter)).append("','YYYY-MM-DD')) ");
    }

    private void equalsOrLike(String value, String attribute) {
        if (value != null) {
            sql.append(" and (").append(attribute).append(" = '").append(value).append("' or ").append(attribute).append(" like '%").append(value).append("%') ");
        }
    }
}
