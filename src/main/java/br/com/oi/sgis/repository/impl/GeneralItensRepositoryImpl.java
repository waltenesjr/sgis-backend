package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.GeneralItensCriteriaDTO;
import br.com.oi.sgis.dto.GeneralItensDTO;
import br.com.oi.sgis.enums.GeneralItensReportBreakEnum;
import br.com.oi.sgis.repository.GeneralItensRepository;
import br.com.oi.sgis.util.MessageUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GeneralItensRepositoryImpl implements GeneralItensRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private StringBuilder sql;

    private GeneralItensCriteriaDTO criteriaDTO;

    @Override
    public List<GeneralItensDTO> listGeneralItens(GeneralItensCriteriaDTO criteriaDTO){
        this.criteriaDTO = criteriaDTO;
        sql = new StringBuilder
                ("select new br.com.oi.sgis.dto.GeneralItensDTO(" +
                " un.id, un.situationCode.id, un.situationDateChange, un.deposit.id," +
                " un.responsible.id, un.location, ea.id, ea.description, me.id, me.description," +
                " un.serieNumber, pt.id, pt.technicianName, un.station.id, ea.mnemonic, epr.companyName," +
                " pt.techPhoneBase, ea.discontinuedFlag, me.descountFlag)  from Unity un left join un.unityCode ea" +
                " left join un.technician pt left join ea.equipModelCode me left join me.equipamentType ste " +
                " left join ste.application ap left join ea.company epr where 1=1 ");

        addCriterias();
        try {
            TypedQuery<GeneralItensDTO> query = entityManager.createQuery(sql.toString(), GeneralItensDTO.class);
            return query.getResultList();
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ERROR_QUERY_REPORT.getDescription());
        }
    }

    private void addCriterias() {
        addSituationPeriodCriteria();
        addSituationCriteria();
        addApplicationCriteria();
        addEquipamentTypeCriteria();
        addModelCriteria();
        addDepositCriteria();
        addResponsibleCriteria();
        addUnityCodeCriteria();
        addTechnicianCriteria();
        addUnityIdCriteria();
        addMnemonicCriteria();
        addStationCriteria();
        addManufacturerCriteria();
        addLocationCriteria();
        addOrderCriteria();
    }

    private void addLocationCriteria() {
        equalsOrLike(criteriaDTO.getLocation(), " un.location ");
    }

    private void addManufacturerCriteria() {
        equalsOrLike(criteriaDTO.getManufacturerId(), " epr.id ");
    }

    private void addStationCriteria() {
        equalsOrLike(criteriaDTO.getStationId(), " un.station.id ");
    }

    private void addMnemonicCriteria() {
        equalsOrLike(criteriaDTO.getMnemonic(), " ea.mnemonic ");
    }

    private void addUnityIdCriteria() {
        equalsOrLike(criteriaDTO.getUnityId(), " un.id ");
    }

    private void addTechnicianCriteria() {
        equalsOrLike(criteriaDTO.getTechnicianId(), " pt.id ");
    }

    private void addResponsibleCriteria() {
        equalsOrLike(criteriaDTO.getResponsibleId(), " un.responsible.id ");
    }

    private void addDepositCriteria() {
        equalsOrLike(criteriaDTO.getDepositaryId(), " un.deposit.id ");
    }

    private void addUnityCodeCriteria() {
        equalsOrLike(criteriaDTO.getUnityCodeId(), " ea.id ");

    }
    private void addModelCriteria() {
        equalsOrLike(criteriaDTO.getEquipamentId(), " me.id ");
    }

    private void addEquipamentTypeCriteria() {
        equalsOrLike(criteriaDTO.getTypeId(), " ste.id ");
    }

    private void addApplicationCriteria() {
        equalsOrLike(criteriaDTO.getApplicationId(), " ap.id ");

    }

    private void addSituationCriteria() {
        equalsOrLike(criteriaDTO.getSituationId(), " un.situationCode.id ");

    }
    private void addSituationPeriodCriteria() {
        conditionalDateBetween(this.criteriaDTO.getSituationInitialDate(),  this.criteriaDTO.getSituationFinalDate(), "un.situationDateChange");

    }

    private void conditionalDateBetween(LocalDateTime initialDate, LocalDateTime finalDate, String value){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if(initialDate!=null && finalDate!=null)
            sql.append("and (").append(value).append(" between TO_DATE('").append(initialDate.format(formatter))
                    .append("','YYYY-MM-DD') and TO_DATE('").append(finalDate.format(formatter)).append("','YYYY-MM-DD')) ");
    }

    private void addOrderCriteria() {
        GeneralItensReportBreakEnum orderEnum = criteriaDTO.getOrder();
        if(orderEnum!=null )
            sql.append(" order by ").append(orderEnum.getAttribute());
    }

    private void equalsOrLike(String value, String attribute) {
        if (value != null) {
            sql.append(" and (").append(attribute).append(" = '").append(value).append("' or ").append(attribute).append(" like '%").append(value).append("%') ");
        }
    }

}
