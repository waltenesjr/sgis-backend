package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.MovItensReportDTO;
import br.com.oi.sgis.entity.view.MovItensView;
import br.com.oi.sgis.enums.MovItensReportOrderEnum;
import br.com.oi.sgis.repository.MovItensRepositoryCustom;
import br.com.oi.sgis.util.MessageUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MovItensRepositoryCustomImpl implements MovItensRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private StringBuilder sql;

    private MovItensReportDTO movItensReportDTO;

    @Override
    public List<MovItensView> findByParameters(MovItensReportDTO movItensReportDTO) {

        this.movItensReportDTO = movItensReportDTO;

        sql = new StringBuilder("select rownum as id, b.mnemonico, b.descricao, a.* " +
                "from v_mov_itens a left join equipamento_area b " +
                "on  a.para_cod_placa = b.cod_area_fabric " +
                "where 1=1 ");

        addCriterias();
        try {
            Query query = entityManager.createNativeQuery(sql.toString(), MovItensView.class);
            return query.getResultList();
        }catch (Exception e ){
            throw new IllegalArgumentException(MessageUtils.ERROR_QUERY_REPORT.getDescription());
        }
    }

    private void addCriterias() {
        addPeriodCriteria();
        addSituationFromCriteria();
        addSituationToCriteria();
        addResponsibleFromCriteria();
        addResponsibleToCriteria();
        addTechinicianFromCriteria();
        addTechinicianToCriteria();
        addUnityCodeCriteria();
        addOrderCriteria();
    }

    private void conditionalDateBetween(LocalDateTime initialDate, LocalDateTime finalDate, String value){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        if(initialDate!=null && finalDate!=null)
            sql.append("and (").append(value).append(" between  TO_DATE('").append(initialDate.format(formatter))
                    .append("',  'DD-MM-YYYY') and TO_DATE('").append(finalDate.format(formatter)).append("', 'DD-MM-YYYY')) ");
    }

    private void addPeriodCriteria() {
        conditionalDateBetween(this.movItensReportDTO.getInitialPeriod(),  this.movItensReportDTO.getEndPeriod(), "a.data");
    }

    private void addOrderCriteria() {
        MovItensReportOrderEnum orderEnum = movItensReportDTO.getOrderBy();
        sql.append(" order by ");
        if(orderEnum!=null && !orderEnum.equals(MovItensReportOrderEnum.DATA))
            sql.append(orderEnum.getOrderBy()).append(", ");
        sql.append(MovItensReportOrderEnum.DATA.getOrderBy());
    }

    private void addUnityCodeCriteria() {
        equalsOrLike(movItensReportDTO.getUnityCode(), " a.para_cod_placa ");
    }

    private void addResponsibleFromCriteria() {
        equalsOrLike(movItensReportDTO.getFromResponsible(), " a.de_sigla_prop ");
    }

    private void addResponsibleToCriteria() {
        equalsOrLike(movItensReportDTO.getToResponsible(), " a.para_sigla_prop ");
    }

    private void addTechinicianFromCriteria() {
        equalsOrLike( movItensReportDTO.getFromTechnician(), " a.de_tecnico ");
    }

    private void addTechinicianToCriteria() {
        equalsOrLike(  movItensReportDTO.getToTechnician(), " a.para_tecnico ");
    }

    private void equalsOrLike(String value, String attribute) {
        if (value != null) {
            sql.append(" and (").append(attribute).append(" = '").append(value).append("' or ").append(attribute).append(" like '%").append(value).append("%') ");
        }
    }

    private void addSituationFromCriteria(){
        List<String> situationsFrom = movItensReportDTO.getFromSituations();
        if(situationsFrom!=null && !situationsFrom.isEmpty())
            sql.append(" and ( a.de_cod_sit in ( ")
                    .append(situationsFrom.stream().collect(Collectors.joining ("','", "'", "'")))
                    .append(" ) )");
    }

    private void addSituationToCriteria(){
        List<String> situationsTo = movItensReportDTO.getToSituations();
        if(situationsTo!=null && !situationsTo.isEmpty()){
            sql.append(" and (a.para_cod_sit in ( ")
                    .append(situationsTo.stream().collect(Collectors.joining ("','", "'", "'")))
                    .append(" ) )");
        }
    }
}
