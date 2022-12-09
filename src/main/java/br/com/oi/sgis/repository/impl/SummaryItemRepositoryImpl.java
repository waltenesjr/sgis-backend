package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.SummaryItemCriteriaReportDTO;
import br.com.oi.sgis.dto.SummaryItemViewDTO;
import br.com.oi.sgis.repository.SummaryItemRepository;
import br.com.oi.sgis.util.MessageUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Locale;

public class SummaryItemRepositoryImpl implements SummaryItemRepository {
    @PersistenceContext
    private EntityManager entityManager;

    private StringBuilder sql;

    private SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO;

    @Override
    public List<SummaryItemViewDTO> findBySummaryParams(SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO) {
        this.summaryItemCriteriaReportDTO = summaryItemCriteriaReportDTO;

        sql = new StringBuilder("select new br.com.oi.sgis.dto.SummaryItemViewDTO(b.equipModelCode.id, c.description, " +
                "                b.id , max(b.description), b.mnemonic, a.station.id, count (a.id)) from Unity a, AreaEquipament b," +
                " ModelEquipamentType c, EquipamentType d where a.situationCode = 'DIS' and a.unityCode.id = b.id and " +
                " b.equipModelCode.id = c.id and c.equipamentType.id = d.id " );

        addCriterias();
        sql.append(" group by b.equipModelCode.id, c.description, b.id, b.mnemonic,  a.station.id");
        orderBy();

        try {
            TypedQuery<SummaryItemViewDTO> query = entityManager.createQuery(sql.toString(), SummaryItemViewDTO.class);
            return query.getResultList();
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ERROR_QUERY_REPORT.getDescription());
        }
    }

    private void addCriterias(){
        applicationLike();
        equipamentTypeLike();
        modelEquipTypeLike();
        unityCodeLike();
        companyLike();
        responsibleLike();
        mnemonicLike();
        stationLike();
    }

    private void orderBy() {
        if(summaryItemCriteriaReportDTO.getBreakResults()!=null){
            sql.append(" order by ").append(summaryItemCriteriaReportDTO.getBreakResults().getAttribute());
        }
    }

    private void stationLike() {
        conditionalLike(summaryItemCriteriaReportDTO.getStationCode(), "a.station.id");
    }

    private void mnemonicLike() {
        conditionalLike(summaryItemCriteriaReportDTO.getMnemonic(), "b.mnemonic");
    }

    private void responsibleLike() {
        conditionalLike(summaryItemCriteriaReportDTO.getResponsibleCode(), "a.responsible.id");
    }

    private void companyLike() {
        conditionalLike(summaryItemCriteriaReportDTO.getResponsibleCode(), "b.company.id");
    }

    private void unityCodeLike() {
        conditionalLike(summaryItemCriteriaReportDTO.getUnityCode(), "b.id");
    }

    private void modelEquipTypeLike() {
        conditionalLike(summaryItemCriteriaReportDTO.getModelCode(), "b.equipModelCode.id");
    }

    private void equipamentTypeLike() {
        conditionalLike(summaryItemCriteriaReportDTO.getTypeCode(), "c.equipamentType.id");
    }

    private void applicationLike() {
        conditionalLike(summaryItemCriteriaReportDTO.getApplicationCode(), "d.application.id");
    }

    private void conditionalLike(String value, String conditional) {
        if(value!=null)
            sql.append("and (upper(").append(conditional).append(") like '%").append(value.toUpperCase(Locale.ROOT)).append("%') ");
    }
}
