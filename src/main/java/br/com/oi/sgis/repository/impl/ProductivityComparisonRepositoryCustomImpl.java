package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.ProductivityComparisonDTO;
import br.com.oi.sgis.repository.ProductivityComparisonRepositoryCustom;
import br.com.oi.sgis.util.MessageUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ProductivityComparisonRepositoryCustomImpl implements ProductivityComparisonRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private StringBuilder sql;

    @Override
    public List<ProductivityComparisonDTO> findProductivityComparisonByTechnical(String repairCenter, String technicalStaffName, LocalDateTime initialDate, LocalDateTime finalDate) {
        sql = new StringBuilder("SELECT A.B_NUM_BR                                                                      as id, " +
                "       C.COD_MATRICULA_TEC                                                             AS technical_staff_cod, " +
                "       MAX(C.NOME_TECNICO)                                                             AS technical_staff_name, " +
                "       A.B_NUM_BR                                                                      AS br_number, " +
                "       MAX(A.B_COD_SIT_REP)                                                            AS COD_SIT_REP, " +
                "       MAX(A.B_SIGLA_CR)                                                               AS B_SIGLA_CR, " +
                "       MAX(E.SR_DESCRICAO)                                                             AS SITUACAO, " +
                "       MAX(F.COD_AREA_FABRIC)                                                          AS unity, " +
                "       MAX(F.DESCRICAO)                                                                AS unity_description, " +
                "       ROUND(SUM(B.IB_DATA_FIM - B.IB_DATA_INI), 2)                                    AS spent_time, " +
                "       ROUND(MAX(A.B_DATA_ENCERRA - A.B_DATA_ACEIT), 2)                                AS repair_time, " +
                "       MAX(B_VALOR_REPARO)                                                             AS repair_value, " +
                "       D.UN_COD_PLACA, " +
                "       SUM(DECODE(A.B_COD_SIT_REP, 'CON', 1, 'AJU', 1, 0))                             as INTERNO, " +
                "       SUM(DECODE(A.B_COD_SIT_REP, 'ORC', 1, 'CTR', 1, 0))                             AS EXTERNO, " +
                "       SUM(DECODE(A.B_COD_SIT_REP, 'GAR', 1, 'GCT', 1, 'GOR', 1, 0))                   AS GARANTIA, " +
                "       ROUND(SUM(DECODE(A.B_COD_SIT_REP, 'CON', A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 'AJU', " +
                "                        A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 0)) / " +
                "             DECODE(SUM(DECODE(A.B_COD_SIT_REP, 'CON', 1, 'AJU', 1, 0)), 0, 1, " +
                "                    SUM(DECODE(A.B_COD_SIT_REP, 'CON', 1, 'AJU', 1, 0))), 2)           as average_int_time, " +
                "       ROUND(SUM(DECODE(A.B_COD_SIT_REP, 'ORC', A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 'CTR', " +
                "                        A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 0)) / " +
                "             DECODE(SUM(DECODE(A.B_COD_SIT_REP, 'ORC', 1, 'CTR', 1, 0)), 0, 1, " +
                "                    SUM(DECODE(A.B_COD_SIT_REP, 'ORC', 1, 'CTR', 1, 0))), 2)           as average_ext_time, " +
                "       ROUND(SUM(DECODE(A.B_COD_SIT_REP, 'GAR', A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 'GCT', " +
                "                        A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 'GOR', A.B_DATA_ENCERRA - A.B_DATA_ACEIT, 0)) / " +
                "             DECODE(SUM(DECODE(A.B_COD_SIT_REP, 'GAR', 1, 'GCT', 1, 'GOR', 1, 0)), 0, 1, " +
                "                    SUM(DECODE(A.B_COD_SIT_REP, 'GAR', 1, 'GCT', 1, 'GOR', 1, 0))), 2) as T_GARANTIA, " +
                "       ROUND(SUM(DECODE(A.B_COD_SIT_REP, 'CON', A.B_VALOR_REPARO, 'AJU', A.B_VALOR_REPARO, 0)) / " +
                "             DECODE(SUM(DECODE(A.B_COD_SIT_REP, 'CON', 1, 'AJU', 1, 0)), 0, 1, " +
                "                    SUM(DECODE(A.B_COD_SIT_REP, 'CON', 1, 'AJU', 1, 0))), 2)           as average_int_value, " +
                "       ROUND(SUM(DECODE(A.B_COD_SIT_REP, 'ORC', A.B_VALOR_REPARO, 'CTR', A.B_VALOR_REPARO, 0)) / " +
                "             DECODE(SUM(DECODE(A.B_COD_SIT_REP, 'ORC', 1, 'CTR', 1, 0)), 0, 1, " +
                "                    SUM(DECODE(A.B_COD_SIT_REP, 'ORC', 1, 'CTR', 1, 0))), 2)           AS average_ext_value " +
                "FROM BILHETE A, " +
                "     INTERVBILH B, " +
                "     PESSOAL_TECNICO C, " +
                "     UNIDADES D, " +
                "     SITUACAO_REP E, " +
                "     EQUIPAMENTO_AREA F " +
                "WHERE A.B_NUM_BR = B.IB_NUM_BR " +
                "  AND B.IB_TECNICO = C.COD_MATRICULA_TEC " +
                "  AND A.B_COD_BARRAS = D.UN_COD_BARRAS " +
                "  AND A.B_COD_SIT_REP = E.SR_COD_SIT_REP " +
                "  AND D.UN_COD_PLACA = F.COD_AREA_FABRIC " +
                "  AND A.B_DATA_ENCERRA IS NOT NULL " +
                "  AND B.IB_EXTERNO = 'N' ");
        addCriterias(repairCenter, technicalStaffName, initialDate, finalDate);
                sql.append(" GROUP BY C.COD_MATRICULA_TEC, A.B_NUM_BR, D.UN_COD_PLACA " +
                            " ORDER BY C.COD_MATRICULA_TEC, MAX(F.COD_AREA_FABRIC)");

        try {
            Query query = entityManager.createNativeQuery(sql.toString(), ProductivityComparisonDTO.class);
            return  query.getResultList();
        }catch (Exception e ){
            throw new IllegalArgumentException(MessageUtils.ERROR_QUERY_REPORT.getDescription());
        }
    }

    private void addCriterias(String repairCenter, String technicalStaffName, LocalDateTime initialDate, LocalDateTime finalDate) {
        addRepairCenterCriteria(repairCenter);
        addTechnicalStaffNameCriteria(technicalStaffName);
        addPeriodCriteria(initialDate, finalDate);
    }

    private void addRepairCenterCriteria(String repairCenter) {
        if (repairCenter != null) {
            sql.append(" and (").append("A.B_SIGLA_CR").append(" = '").append(repairCenter).append("' or ").append("A.B_SIGLA_CR").append(" like '%").append(repairCenter).append("%') ");
        }
    }

    private void addTechnicalStaffNameCriteria(String technicalStaffName) {
        if (technicalStaffName != null) {
            sql.append(" and (").append("C.NOME_TECNICO").append(" = '").append(technicalStaffName).append("' or ").append("C.NOME_TECNICO").append(" like '%").append(technicalStaffName).append("%') ");
        }
    }

    private void addPeriodCriteria(LocalDateTime initialDate, LocalDateTime finalDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if(initialDate!=null && finalDate!=null)
            sql.append("and (").append("A.B_DATA").append(" between to_date('").append(initialDate.format(formatter))
                    .append("', 'YYYY-MM-DD')  and to_date('").append(finalDate.format(formatter)).append("', 'YYYY-MM-DD')) ");
    }
}
