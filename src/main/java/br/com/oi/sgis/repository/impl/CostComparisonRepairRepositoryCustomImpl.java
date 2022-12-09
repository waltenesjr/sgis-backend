package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.CostComparisonRepDTO;
import br.com.oi.sgis.repository.CostComparisonRepairRepositoryCustom;
import br.com.oi.sgis.util.MessageUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CostComparisonRepairRepositoryCustomImpl implements CostComparisonRepairRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    private StringBuilder sql;

    @Override
    public List<CostComparisonRepDTO> findCostRepair(String repairCenter, LocalDateTime initialDate, LocalDateTime finalDate) {
        sql = new StringBuilder(" select max( C.COD_AREA_FABRIC  )                                                                                 as id, " +
                "       A.B_SIGLA_CR                                                                                          as repair_center, " +
                "       C.COD_AREA_FABRIC                                                                                     AS equipment, " +
                "       MAX(C.DESCRICAO)                                                                                      AS description, " +
                "       D.COD_MODELO_EQTO                                                                                     as equipment_model, " +
                "       MAX(D.DESCR_TIPO_MODELO)                                                                              as equipment_model_description, " +
                "       E.COD_TIPO_EQTO                                                                                       as equipment_type, " +
                "       MAX(E.NOME_TIPO_EQTO)                                                                                 as equipment_type_description, " +
                "       SUM(DECODE(A.B_COD_SIT_REP, 'CON', 1, 'AJU', 1, 0))                                                   as internal, " +
                "       SUM(DECODE(A.B_COD_SIT_REP, 'ORC', 1, 'CTR', 1, 0))                                                   AS external, " +
                "       SUM(DECODE(A.B_COD_SIT_REP, 'GAR', 1, 'GCT', 1, 'GOR', 1, 0))                                         AS warranty, " +
                "       SUM(DECODE(A.B_COD_SIT_REP, 'SCR', 1, 'SCE', 1, 0))                                                   AS waste, " +
                "       SUM(DECODE(A.B_COD_SIT_REP, 'SDE', 1, 0))                                                             AS no_defect, " +
                "       SUM(DECODE(A.B_COD_SIT_REP, 'CON', A.B_VALOR_REPARO, 'AJU', A.B_VALOR_REPARO, " +
                "                  0))                                                                                        as value_ri, " +
                "       SUM(DECODE(A.B_COD_SIT_REP, 'ORC', A.B_VALOR_REPARO, 'CTR', A.B_VALOR_REPARO, " +
                "                  0))                                                                                        AS value_re, " +
                "       SUM(DECODE(A.B_COD_SIT_REP, 'GAR', A.B_VALOR_REPARO, 'GCT', A.B_VALOR_REPARO, 'GOR', A.B_VALOR_REPARO, " +
                "                  0))                                                                                        AS value_warranty, " +
                "       ROUND(SUM(DECODE(A.B_COD_SIT_REP, 'CON', A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 'AJU', " +
                "                        A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 0)), 2)                                           as time_ri, " +
                "       ROUND(SUM(DECODE(A.B_COD_SIT_REP, 'ORC', A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 'CTR', " +
                "                        A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 0)), 2)                                           AS time_re, " +
                "       ROUND(SUM(DECODE(A.B_COD_SIT_REP, 'GAR', A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 'GCT', " +
                "                        A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 'GOR', A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 0)), " +
                "             2)                                                                                              AS time_warranty, " +
                "       ROUND(SUM(DECODE(A.B_COD_SIT_REP, 'CON', A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 'AJU', " +
                "                        A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 0)) / " +
                "             DECODE(SUM(DECODE(A.B_COD_SIT_REP, 'CON', 1, 'AJU', 1, 0)), 0, 1, " +
                "                    SUM(DECODE(A.B_COD_SIT_REP, 'CON', 1, 'AJU', 1, 0))), " +
                "             2)                                                                                              as internal_total, " +
                "       ROUND(SUM(DECODE(A.B_COD_SIT_REP, 'ORC', A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 'CTR', " +
                "                        A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 0)) / " +
                "             DECODE(SUM(DECODE(A.B_COD_SIT_REP, 'ORC', 1, 'CTR', 1, 0)), 0, 1, " +
                "                    SUM(DECODE(A.B_COD_SIT_REP, 'ORC', 1, 'CTR', 1, 0))), " +
                "             2)                                                                                              as external_total, " +
                "       ROUND(SUM(DECODE(A.B_COD_SIT_REP, 'GAR', A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 'GCT', " +
                "                        A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 'GOR', A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 0)) / " +
                "             DECODE(SUM(DECODE(A.B_COD_SIT_REP, 'GAR', 1, 'GCT', 1, 'GOR', 1, 0)), 0, 1, " +
                "                    SUM(DECODE(A.B_COD_SIT_REP, 'GAR', 1, 'GCT', 1, 'GOR', 1, 0))), " +
                "             2)                                                                                              as warranty_total, " +
                "       ROUND(SUM(DECODE(A.B_COD_SIT_REP, 'CON', A.B_VALOR_REPARO, 'AJU', A.B_VALOR_REPARO, 0)) / " +
                "             DECODE(SUM(DECODE(A.B_COD_SIT_REP, 'CON', 1, 'AJU', 1, 0)), 0, 1, " +
                "                    SUM(DECODE(A.B_COD_SIT_REP, 'CON', 1, 'AJU', 1, 0))), " +
                "             2)                                                                                              as value_unity_ri, " +
                "       ROUND(SUM(DECODE(A.B_COD_SIT_REP, 'ORC', A.B_VALOR_REPARO, 'CTR', A.B_VALOR_REPARO, 0)) / " +
                "             DECODE(SUM(DECODE(A.B_COD_SIT_REP, 'ORC', 1, 'CTR', 1, 0)), 0, 1, " +
                "                    SUM(DECODE(A.B_COD_SIT_REP, 'ORC', 1, 'CTR', 1, 0))), " +
                "             2)                                                                                              AS value_unity_re " +
                "from BILHETE A, " +
                "     UNIDADES B, " +
                "     EQUIPAMENTO_AREA C, " +
                "     TIPO_MODELO_EQPTO D, " +
                "     SGE_TIPO_EQPTO E " +
                "where A.B_COD_BARRAS = B.UN_COD_BARRAS " +
                "  AND B.UN_COD_PLACA = C.COD_AREA_FABRIC " +
                "  AND C.COD_MODELO_EQTO = D.COD_MODELO_EQTO " +
                "  AND D.COD_TIPO_EQTO = E.COD_TIPO_EQTO " +
                "  AND A.B_DATA_ENCERRA IS NOT NULL");
        addCriterias(repairCenter, initialDate, finalDate);
        sql.append(" GROUP BY A.B_SIGLA_CR, E.COD_TIPO_EQTO , D.COD_MODELO_EQTO , C.COD_AREA_FABRIC ");
        sql.append(" ORDER BY A.B_SIGLA_CR, E.COD_TIPO_EQTO , D.COD_MODELO_EQTO , C.COD_AREA_FABRIC ");

        try {
            Query query = entityManager.createNativeQuery(sql.toString(), CostComparisonRepDTO.class);
            return  query.getResultList();
        }catch (Exception e ){
            throw new IllegalArgumentException(MessageUtils.ERROR_QUERY_REPORT.getDescription());
        }
    }

    private void addCriterias(String repairCenter, LocalDateTime initialDate, LocalDateTime finalDate) {
        addRepairCenterCriteria(repairCenter);
        addPeriodCriteria(initialDate, finalDate);
    }

    private void addRepairCenterCriteria(String repairCenter) {
        if (repairCenter != null) {
            sql.append(" and (").append("A.B_SIGLA_CR").append(" = '").append(repairCenter).append("' or ").append("A.B_SIGLA_CR").append(" like '%").append(repairCenter).append("%') ");
        }
    }

    private void addPeriodCriteria(LocalDateTime initialDate, LocalDateTime finalDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if(initialDate!=null && finalDate!=null)
            sql.append("and (").append("A.B_DATA").append(" between to_date('").append(initialDate.format(formatter))
                    .append("', 'YYYY-MM-DD') and to_date('").append(finalDate.format(formatter)).append("', 'YYYY-MM-DD')) ");
    }



}
