package br.com.oi.sgis.repository.impl;

import br.com.oi.sgis.dto.StockSummaryCriteriaDTO;
import br.com.oi.sgis.dto.StockSummaryDTO;
import br.com.oi.sgis.repository.StockSummaryRepository;
import br.com.oi.sgis.util.MessageUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Locale;

public class StockSummaryRepositoryImpl implements StockSummaryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private StringBuilder sql;

    private StockSummaryCriteriaDTO stockSummaryCriteriaDTO;


    @Override
    public List<StockSummaryDTO> findBySummaryParams(StockSummaryCriteriaDTO stockSummaryCriteriaDTO) {
        this.stockSummaryCriteriaDTO = stockSummaryCriteriaDTO;

        sql = new StringBuilder("select   u.un_cod_placa as id, u.un_sigla_prop as responsible, u.un_cod_placa as unity, " +
                " max(du.du_inativa) as inactive," +
                " max(case when ae.mnemonico is null then ae.descricao else   ae.mnemonico|| ' - ' || ae.descricao end)  as description, " +
                " max(du.du_estoque_minimo) as min, " +
                " max(du.du_estoque_repos) as repos, max(du.du_estoque_maximo) as max, " +
                " max(du.du_localizacao) as location, max(du.du_estacao) as station, " +
                " sum(case when u.un_cod_sit='TRN' then 1 else 0 end) as trn, " +
                "sum(case when u.un_cod_sit='DIS' then 1 else 0 end) as dis, " +
                "sum(case when u.un_cod_sit='EMP' then 1 else 0 end) as emp, " +
                "sum(case when u.un_cod_sit='TDR' then 1 else 0 end) as tdr, " +
                "sum(case when u.un_cod_sit='EMU' then 1 else 0 end) as emu, " +
                "sum(case when u.un_cod_sit='TRR' then 1 else 0 end) as trr, " +
                "sum(case when u.un_cod_sit='BXP' then 1 else 0 end) as bxp, " +
                "sum(case when u.un_cod_sit='TRP' then 1 else 0 end) as trp, " +
                "sum(case when u.un_cod_sit='TRE' then 1 else 0 end) as tre, " +
                "sum(case when u.un_cod_sit='REP' then 1 else 0 end) as rep, " +
                "sum(case when u.un_cod_sit='BXU' then 1 else 0 end) as bxu, " +
                "sum(case when u.un_cod_sit='BXE' then 1 else 0 end) as bxe, " +
                "sum(case when u.un_cod_sit='DVR' then 1 else 0 end) as dvr, " +
                "sum(case when u.un_cod_sit='TRD' then 1 else 0 end) as trd, " +
                "sum(case when u.un_cod_sit='USO' then 1 else 0 end) as uso, " +
                "sum(case when u.un_cod_sit='BXI' then 1 else 0 end) as bxi, " +
                "sum(case when u.un_cod_sit='PRE' then 1 else 0 end) as pre, " +
                "sum(case when u.un_cod_sit='BXC' then 1 else 0 end) as bxc, " +
                "sum(case when u.un_cod_sit='BXO' then 1 else 0 end) as bxo, " +
                "sum(case when u.un_cod_sit='RES' then 1 else 0 end) as res, " +
                "sum(case when u.un_cod_sit='OFE' then 1 else 0 end) as ofe, " +
                "sum(case when u.un_cod_sit='DEF' then 1 else 0 end) as def, " +
                "sum(case when u.un_cod_sit='TDD' then 1 else 0 end) as tdd," +
                "0 as disponible, 0 as reposition, 0 as off " +
                "from unidades u inner join  equipamento_area ae on u.un_cod_placa  = ae.cod_area_fabric " +
                "left join DEPTOS_UNIDADES du on  du.du_cod_placa=u.un_cod_placa and du.du_sigla=u.un_sigla_prop  where 1=1 ");
        addCriterias();
        sql.append(" group by u.un_sigla_prop , u.un_cod_placa,  u.un_cod_placa   ");
        addHaving();
        addUnion();
        sql.append(" order by 1,2");

        try {
            Query query = entityManager.createNativeQuery(sql.toString(), StockSummaryDTO.class);
            return  query.getResultList();
        }catch (Exception e ){
            throw new IllegalArgumentException(MessageUtils.ERROR_QUERY_REPORT.getDescription());
        }
    }

    private void addUnion() {
        sql.append(" union select   u.un_cod_placa  as id, du.du_sigla as responsible, du.du_cod_placa as unity, max(du.du_inativa) as inactive," +
                " max(case when ae.mnemonico is null then ae.descricao else   ae.mnemonico|| ' - ' || ae.descricao end) as description, " +
                " max(du.du_estoque_minimo) as min, max(du.du_estoque_repos) as repos, max(du.du_estoque_maximo) as max, " +
                " max(du.du_localizacao) as location, max(du.du_estacao) as station, " +
                " sum(case when u.un_cod_sit='TRN' then 1 else 0 end) as trn, " +
                "sum(case when u.un_cod_sit='DIS' then 1 else 0 end) as dis, " +
                "sum(case when u.un_cod_sit='EMP' then 1 else 0 end) as emp, " +
                "sum(case when u.un_cod_sit='TDR' then 1 else 0 end) as tdr, " +
                "sum(case when u.un_cod_sit='EMU' then 1 else 0 end) as emu, " +
                "sum(case when u.un_cod_sit='TRR' then 1 else 0 end) as trr, " +
                "sum(case when u.un_cod_sit='BXP' then 1 else 0 end) as bxp, " +
                "sum(case when u.un_cod_sit='TRP' then 1 else 0 end) as trp, " +
                "sum(case when u.un_cod_sit='TRE' then 1 else 0 end) as tre, " +
                "sum(case when u.un_cod_sit='REP' then 1 else 0 end) as rep, " +
                "sum(case when u.un_cod_sit='BXU' then 1 else 0 end) as bxu, " +
                "sum(case when u.un_cod_sit='BXE' then 1 else 0 end) as bxe, " +
                "sum(case when u.un_cod_sit='DVR' then 1 else 0 end) as dvr, " +
                "sum(case when u.un_cod_sit='TRD' then 1 else 0 end) as trd, " +
                "sum(case when u.un_cod_sit='USO' then 1 else 0 end) as uso, " +
                "sum(case when u.un_cod_sit='BXI' then 1 else 0 end) as bxi, " +
                "sum(case when u.un_cod_sit='PRE' then 1 else 0 end) as pre, " +
                "sum(case when u.un_cod_sit='BXC' then 1 else 0 end) as bxc, " +
                "sum(case when u.un_cod_sit='BXO' then 1 else 0 end) as bxo, " +
                "sum(case when u.un_cod_sit='RES' then 1 else 0 end) as res, " +
                "sum(case when u.un_cod_sit='OFE' then 1 else 0 end) as ofe, " +
                "sum(case when u.un_cod_sit='DEF' then 1 else 0 end) as def, " +
                "sum(case when u.un_cod_sit='TDD' then 1 else 0 end) as tdd," +
                "0 as disponible, 0 as reposition, 0 as off " +
                "from deptos_unidades du inner join equipamento_area ae on du.du_cod_placa=ae.cod_area_fabric " +
                "left join unidades u on  du.du_cod_placa=u.un_cod_placa and du.du_sigla=u.un_sigla_prop  " +
                "where du.du_inativa='N' ");
        addCriterias();
        sql.append(" group by du.du_sigla , du.du_cod_placa,  u.un_cod_placa   ");
        addHavingUnion();
    }

    private void addCriterias() {
        departmantLike();
        modelLike();
    }

    private void addHaving() {
        String having = " having max(coalesce(du.du_inativa, 'N')) = 'N' AND (sum(case when u.un_cod_sit = 'DIS' then 1 else 0 end) + " +
                " sum(case when u.un_cod_sit = 'EMP' then 1 else 0 end) + sum(case when u.un_cod_sit = 'EMU' then 1 else 0 end) +" +
                " sum(case when u.un_cod_sit = 'RES' then 1 else 0 end) + sum(case when u.un_cod_sit = 'TRD' then 1 else 0 end) +" +
                " sum(case when u.un_cod_sit = 'TRE' then 1 else 0 end) + sum(case when u.un_cod_sit = 'USO' then 1 else 0 end) )" +
                " ";
        addFiltering(having);
    }

    private void addHavingUnion() {
        String having = " having (sum(case when u.un_cod_sit = 'DIS' then 1 else 0 end) + " +
                " sum(case when u.un_cod_sit = 'EMP' then 1 else 0 end) + sum(case when u.un_cod_sit = 'EMU' then 1 else 0 end) +" +
                " sum(case when u.un_cod_sit = 'RES' then 1 else 0 end) + sum(case when u.un_cod_sit = 'TRD' then 1 else 0 end) +" +
                " sum(case when u.un_cod_sit = 'TRE' then 1 else 0 end) + sum(case when u.un_cod_sit = 'USO' then 1 else 0 end) )" +
                " ";
        addFiltering(having);
    }

    private void addFiltering(String having) {
        switch (stockSummaryCriteriaDTO.getFiltering()){
            case MAXIMO:
                sql.append(having).append(">= coalesce( MAX(DU_ESTOQUE_MAXIMO),0)");
                break;
            case REPOSICAO:
                sql.append(having).append("<= coalesce( MAX(DU_ESTOQUE_REPOS),0)");
                break;
            case MINIMO:
                sql.append(having).append("<= coalesce( MAX(DU_ESTOQUE_MINIMO),0)");
                break;
            default: sql.append("");
        }
    }

    private void departmantLike() {
        conditionalLike(stockSummaryCriteriaDTO.getResponsibleCode(), "and", "u.un_sigla_prop");
    }

    private void modelLike() {
        if(stockSummaryCriteriaDTO.getModelCode()!=null) {
            conditionalLike(stockSummaryCriteriaDTO.getModelCode(), "and","u.un_cod_placa");
        }
    }
    private void conditionalLike(String value, String conditional, String attribute) {
        sql.append(conditional).append(" (upper(").append(attribute).append(") like '%")
                .append(value.toUpperCase(Locale.ROOT)).append("%') ");
    }
}
