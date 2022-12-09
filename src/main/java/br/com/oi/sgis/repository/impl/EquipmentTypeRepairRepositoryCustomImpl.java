package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.EquipamentTypeRepairDTO;
import br.com.oi.sgis.repository.EquipmentTypeRepairRepositoryCustom;
import br.com.oi.sgis.util.MessageUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EquipmentTypeRepairRepositoryCustomImpl implements EquipmentTypeRepairRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private StringBuilder sql;

    @Override
    public List<EquipamentTypeRepairDTO> findByRepairCenter(String repairCenter, LocalDateTime initialDate, LocalDateTime finalDate) {
        sql = new StringBuilder("SELECT E.COD_TIPO_EQTO                                                                                  as id," +
                "       A.B_SIGLA_CR                                                                                     as repair_center," +
                "       E.COD_TIPO_EQTO                                                                                  as equipment," +
                "       MAX(E.NOME_TIPO_EQTO)                                                                            AS description," +
                "       NVL(SUM(DECODE(B_COD_SIT_REP, 'CON', 1, 'SCR', 1, 'SDE', 1, 'AJU', 1)), 0)                       as RI," +
                "       NVL(SUM(DECODE(B_COD_SIT_REP, 'ORC', 1, 'CTR', 1, 'SCE', 1)), 0)                                 as normal_re," +
                "       NVL(SUM(DECODE(B_COD_SIT_REP, 'GAR', 1, 'GCT', 1, 'GOR', 1)), 0)                                 as warranty_re," +
                "       TRUNC(SUM(DECODE(B_COD_SIT_REP, 'CON', B_DATA_ENCERRA - NVL(B_DATA_ACEIT, B_DATA), 'SCR'," +
                "                        B_DATA_ENCERRA - NVL(B_DATA_ACEIT, B_DATA), 'SDE', B_DATA_ENCERRA - NVL(B_DATA_ACEIT, B_DATA)," +
                "                        'AJU', B_DATA_ENCERRA - NVL(B_DATA_ACEIT, B_DATA))) /" +
                "             SUM(DECODE(B_COD_SIT_REP, 'CON', 1, 'SCR', 1, 'SDE', 1, 'AJU', 1)), 2)                     as time_ri," +
                "       TRUNC(SUM(DECODE(B_COD_SIT_REP, 'GAR', B_DATA_ENCERRA - NVL(B_DATA_ACEIT, B_DATA), 'ORC'," +
                "                        B_DATA_ENCERRA - NVL(B_DATA_ACEIT, B_DATA), 'CTR', B_DATA_ENCERRA - NVL(B_DATA_ACEIT, B_DATA)," +
                "                        'GCT', B_DATA_ENCERRA - NVL(B_DATA_ACEIT, B_DATA), 'GOR'," +
                "                        B_DATA_ENCERRA - NVL(B_DATA_ACEIT, B_DATA), 'SCE'," +
                "                        B_DATA_ENCERRA - NVL(B_DATA_ACEIT, B_DATA))) /" +
                "             SUM(DECODE(B_COD_SIT_REP, 'ORC', 1, 'CTR', 1, 'SCE', 1, 'GAR', 1, 'GCT', 1, 'GOR', 1)), 2) as time_re," +
                "       ROUND(SUM(DECODE(B_COD_SIT_REP, 'CON', 1, 'SCR', 1, 'SDE', 1, 'AJU', 1)) /" +
                "             SUM(DECODE(B_COD_SIT_REP, 'CON', 1, 'SCR', 1, 'SDE', 1, 'AJU', 1, 'GAR', 1, 'ORC', 1, 'CTR', 1, 'GCT', 1," +
                "                        'GOR', 1, 'SCE', 1)) * 100, 2)                                                  as perc_ri," +
                "       ROUND(SUM(DECODE(B_COD_SIT_REP, 'GAR', 1, 'ORC', 1, 'CTR', 1, 'GCT', 1, 'GOR', 1, 'SCE', 1)) /" +
                "             SUM(DECODE(B_COD_SIT_REP, 'CON', 1, 'SCR', 1, 'SDE', 1, 'AJU', 1, 'GAR', 1, 'ORC', 1, 'CTR', 1, 'GCT', 1," +
                "                        'GOR', 1, 'SCE', 1)) * 100, 2)                                                  as PERC_RE," +
                "       NVL(round(SUM(DECODE(B_COD_SIT_REP, 'CON', B_VALOR_REPARO, 'SCR', B_VALOR_REPARO, 'SDE', B_VALOR_REPARO, 'AJU'," +
                "                            B_VALOR_REPARO)) / SUM(DECODE(B_COD_SIT_REP, 'CON', 1, 'SCR', 1, 'SDE', 1, 'AJU', 1)), 2)," +
                "           0)                                                                                           as value_unity_ri," +
                "       NVL(round(SUM(DECODE(B_COD_SIT_REP, 'ORC', B_VALOR_REPARO, 'CTR', B_VALOR_REPARO, 'SCE', B_VALOR_REPARO)) /" +
                "                 SUM(DECODE(B_COD_SIT_REP, 'ORC', 1, 'CTR', 1, 'SCE', 1)), 2)," +
                "           0)                                                                                           as value_unity_re," +
                "       NVL(round(SUM(DECODE(B_COD_SIT_REP, 'CON', B_VALOR_REPARO, 'SCR', B_VALOR_REPARO, 'SDE', B_VALOR_REPARO, 'AJU'," +
                "                            B_VALOR_REPARO)), 2), 0)                                                    as value_ri," +
                "       NVL(round(SUM(DECODE(B_COD_SIT_REP, 'ORC', B_VALOR_REPARO, 'CTR', B_VALOR_REPARO, 'SCE', B_VALOR_REPARO)), 2)," +
                "           0)                                                                                           as value_re," +
                "       NVL(SUM(DECODE(B_COD_SIT_REP, 'ABE', 1)), 0)                                                     as open_not_accepted," +
                "       NVL(SUM(DECODE(B_COD_SIT_REP, 'ACT', 1)), 0)                                                     as open_not_forward," +
                "       NVL(SUM(DECODE(B_COD_SIT_REP, 'ERI', 1)), 0)                                                     as open_ri," +
                "       NVL(SUM(DECODE(B_COD_SIT_REP, 'EGO', 1, 'EGC', 1, 'EGA', 1)), 0)                                 as open_re_gar," +
                "       NVL(SUM(DECODE(B_COD_SIT_REP, 'ECT', 1, 'EOR', 1)), 0)                                           as open_re" +
                " FROM BILHETE A," +
                "     UNIDADES B," +
                "     EQUIPAMENTO_AREA C," +
                "     TIPO_MODELO_EQPTO D," +
                "     SGE_TIPO_EQPTO E" +
                " where A.B_COD_BARRAS = B.UN_COD_BARRAS" +
                "  AND B.UN_COD_PLACA = C.COD_AREA_FABRIC" +
                "  AND C.COD_MODELO_EQTO = D.COD_MODELO_EQTO" +
                "  AND D.COD_TIPO_EQTO = E.COD_TIPO_EQTO");
        addCriterias(repairCenter, initialDate, finalDate);
        sql.append(" GROUP BY  E.COD_TIPO_EQTO , A.B_SIGLA_CR, E.COD_TIPO_EQTO");
        sql.append( " order by A.B_SIGLA_CR, E.COD_TIPO_EQTO");

        try {
            Query query = entityManager.createNativeQuery(sql.toString(), EquipamentTypeRepairDTO.class);
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
