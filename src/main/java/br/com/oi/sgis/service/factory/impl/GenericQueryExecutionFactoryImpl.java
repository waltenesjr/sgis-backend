package br.com.oi.sgis.service.factory.impl;

import br.com.oi.sgis.dto.GenericQueryDTO;
import br.com.oi.sgis.dto.GenericQueryItemDTO;
import br.com.oi.sgis.entity.Domain;
import br.com.oi.sgis.repository.DomainRepository;
import br.com.oi.sgis.service.factory.GenericQueryExecutionFactory;
import br.com.oi.sgis.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class GenericQueryExecutionFactoryImpl implements GenericQueryExecutionFactory {

    private StringBuilder sql;
    private final DomainRepository domainRepository;
    private Map<String, List<Domain>> mapColumns;
    private static final String AND = " AND ";  // Diminuir repetição de String
    private List<String> orderBy = new ArrayList<>();
    @Override
    public String createSqlQuery(GenericQueryDTO genericQueryDTO) {
        sql = new StringBuilder("SELECT ");
        getColumns(genericQueryDTO.getColumns(), genericQueryDTO.getGenericQueryTypeId());
        getTotal(genericQueryDTO.isTotalizeFlag());
        sql.append(" FROM ");
        getTables(genericQueryDTO.getGenericQueryTypeId());
        sql.append( " WHERE 1=1 ");
        getConditions(genericQueryDTO.getColumns(), genericQueryDTO.getGenericQueryTypeId());
        getGroupByTotal(genericQueryDTO.isTotalizeFlag());
        orderByDescr();

        return sql.toString();
    }

    private void orderByDescr() {
        if(orderBy!=null && !orderBy.isEmpty()){
            sql.append(" order  by ")
                    .append(orderBy.stream().map(o -> o.toUpperCase(Locale.ROOT)).collect(Collectors.joining(",")));

        }
    }

    private void getGroupByTotal(boolean totalizeFlag) {
        if(totalizeFlag)
            sql.append( " group by ")
                    .append(mapColumns.values().stream().map(m -> m.get(0).getKey()).collect(Collectors.joining(",")));

    }

    private void getTotal(boolean totalizeFlag) {
        if(totalizeFlag)
            sql.append( ", count(*) as QUANT ");
    }

    private void getConditions(List<GenericQueryItemDTO> columns, String type) {
        for (GenericQueryItemDTO column: columns
        ) {
            String columnValue ;
            if(column.isNoShow()) {
                columnValue = domainRepository.findDistinctByDescriptionInAndDomainID_Id(List.of(column.getColumnName()), type).get(0).getKey();
            }
            else {
                columnValue = mapColumns.get(column.getColumnName()).get(0).getKey();
            }
            conditional(column, columnValue);
            conditionalDateBetwwen(column, columnValue);
            conditionalIsNull(column, columnValue);
            conditionalIsNotNull(column, columnValue);
            conditionalOrderBy(column, columnValue);
        }
    }

    private void conditionalOrderBy(GenericQueryItemDTO column, String columnValue) {
        if(column.isFlagOrder() && column.getColumnName().startsWith("Descr_"))
            orderBy.add(columnValue);
    }

    private void conditionalIsNotNull(GenericQueryItemDTO column, String columnValue) {
        if(column.isFlagNonull())
            sql.append(AND).append(columnValue).append( " is not null");
    }

    private void conditionalIsNull(GenericQueryItemDTO column, String columnValue) {
        if(column.isFlagNulls())
            sql.append(AND).append(columnValue).append( " is null");
    }

    private void conditionalDateBetwwen(GenericQueryItemDTO column, String columnValue) {
        if((column.getValueOne()!=null) && (column.getValueTwo() != null)){
            sql.append(AND).append(columnValue)
                    .append(" ").append( " BETWEEN  ").append(" to_date('").append(column.getValueOne())
                    .append("', 'DD/MM/YYYY') and to_date('").append(column.getValueTwo()).append("', 'DD/MM/YYYY') ");
        }
    }

    private void conditional(GenericQueryItemDTO column, String columnValue) {
        if((column.getValueOne()!=null) && (column.getValueTwo() == null )){
            if(Utils.getNumberFromString(column.getValueOne()) != null && !column.getOperator().toUpperCase(Locale.ROOT).equals("LIKE")) {
                sql.append(AND).append(columnValue)
                        .append(" ").append( column.getOperator()).append(" ").append(column.getValueOne());
            }else {
                sql.append(AND).append(columnValue)
                        .append(" ").append(column.getOperator()).append(" '").append(column.getValueOne()).append("'");
            }
        }
    }


    private void getTables(String genericQueryTypeId) {
        defaultTables();
        switch (genericQueryTypeId){
            case "CNU" :  cnuTables();break;
            case "CNR" :  cnrTables();break;
            case "CNI" :  cniTables();break;
            default: cnsTables();
        }

    }

    private void cnsTables() {
        sql.append(" LEFT JOIN DOMINIO DOS on E.UN_STATUS_SAP = DOS.COD_CHAVE_DO AND DOS.COD_DO = 'UN_STATUS_SAP' ")
                .append( " LEFT JOIN EQUIPAMENTO_AREA D1 on D1.COD_AREA_FABRIC = E.UN_COD_PLACA ")
                .append( " LEFT JOIN SWAP_UNIDADES S on S.SW_COD_BARRAS_DE = E.UN_COD_BARRAS ")
                .append( " LEFT JOIN DOMINIO DOS1 on S.SW_STATUS = DOS1.COD_CHAVE_DO AND DOS1.COD_DO = 'SW_STATUS' ")
                .append( " LEFT JOIN UNIDADES E2 ON E2.UN_COD_BARRAS = S.SW_COD_BARRAS_PARA ")
                .append(" LEFT JOIN PESSOAL_TECNICO G on S.SW_TECNICO_SOLIC = G.COD_MATRICULA_TEC ")
                .append( " LEFT JOIN EQUIPAMENTO_AREA D2 ON D2.COD_AREA_FABRIC = E2.UN_COD_PLACA ");
    }

    private void cniTables() {
        cnrTables();
        sql.append( " LEFT JOIN INTERVBILH P on O.B_NUM_BR = P.IB_NUM_BR ")
                .append( " LEFT JOIN ORCAMENTO_ITENS Z on P.IB_NUM_BR = Z.OI_NUM_BR AND P.IB_SEQ = Z.OI_SEQ ")
                .append(" LEFT JOIN ORCAMENTO OR1 on Z.OI_NUM_ORC = OR1.OR_NUM_ORC ");
    }

    private void cnuTables() {
        sql.append(" LEFT JOIN CAIXAS M on E.UN_COD_CAIXA = M.CX_COD_CAIXA")
                .append(" LEFT JOIN TIPO_CAIXA N on M.CX_COD_TIPO_CAIXA = N.TX_COD_TIPO_CAIXA")
                .append(" LEFT JOIN DOMINIO DOB on E.UN_MOTIVO_BAIXA = DOB.COD_CHAVE_DO  AND DOB.COD_DO  = 'UN_MOTIVO_BAIXA'")
                .append(" LEFT JOIN DOMINIO DOI on E.UN_MOTIVO_INSTALACAO = DOI.COD_CHAVE_DO AND DOI.COD_DO = 'UN_MOTIVO_INSTALACAO'")
                .append(" LEFT JOIN DOMINIO DOC on E.UN_MOTIVO_CADASTRO = DOC.COD_CHAVE_DO AND DOC.COD_DO = 'UN_MOTIVO_CADASTRO'")
                .append(" LEFT JOIN V_ULTIMA_OPERACAO_SAP VUO on E.UN_COD_BARRAS = VUO.COD_BARRAS  ")
                .append(" LEFT JOIN DOMINIO DOO on VUO.ULTIMA_OPERACAO_SAP = DOO.COD_CHAVE_DO AND DOO.COD_DO = 'OPERACAO'")
                .append(" LEFT JOIN DOMINIO DOS on E.UN_STATUS_SAP = DOS.COD_CHAVE_DO AND DOS.COD_DO = 'UN_STATUS_SAP'")
                .append(" LEFT JOIN DOCUMENTOS_FISCAIS DF on E.UN_NF_CGC_CPF = DF.DF_CGC_CPF AND E.UN_NF_NUM_DOC = DF.DF_NUM_DOC AND E.UN_NF_DATA_DOC = DF.DF_DATA_DOC")
                .append(" LEFT JOIN EMPRESAS J3 on DF.DF_CGC_CPF = J3.E_CGC_CPF")
                .append(" LEFT JOIN TECNICA TE on D.COD_TECNICA = TE.TC_CODIGO ")
                .append(" LEFT JOIN EMPRESAS J on C.E_CGC_CPF = J.E_CGC_CPF ")
                .append(" LEFT JOIN EMPRESAS J1 on D.E_CGC_CPF = J1.E_CGC_CPF ")
                .append(" LEFT JOIN EMPRESAS L on E.UN_CGC_CPF_INST = L.E_CGC_CPF ")
                .append(" LEFT JOIN EMPRESAS K on E.UN_CGC_CPF_RESP = K.E_CGC_CPF ")
                .append(" LEFT JOIN DEPTOS H on E.UN_SIGLA_PROP = H.D_SIGLA ")
                .append(" LEFT JOIN SITUACAO F on E.UN_COD_SIT = F.S_COD_SIT ")
                .append(" LEFT JOIN PESSOAL_TECNICO G on E.UN_TECNICO = G.COD_MATRICULA_TEC ")
                .append(" LEFT JOIN PESSOAL_TECNICO TX on E.UN_TECNICO_INSTALA = TX.COD_MATRICULA_TEC ")
                .append(" LEFT JOIN DEPTOS I ON E.UN_SIGLA_LOCAL = I.D_SIGLA ");
    }

    private void defaultTables() {
        sql.append( " APLICACAO A ")
                .append(" LEFT JOIN SGE_TIPO_EQPTO B on A.AP_COD_APLICACAO = B.AP_COD_APLICACAO ")
                .append(" LEFT JOIN TIPO_MODELO_EQPTO C on B.COD_TIPO_EQTO = C.COD_TIPO_EQTO ")
                .append(" LEFT JOIN EQUIPAMENTO_AREA D on C.COD_MODELO_EQTO = D.COD_MODELO_EQTO ")
                .append(" LEFT JOIN UNIDADES E on D.COD_AREA_FABRIC = E.UN_COD_PLACA ");
    }

    private void cnrTables() {
        cnuTables();
        sql.append(" LEFT JOIN BILHETE O on E.UN_COD_BARRAS = O.B_COD_BARRAS ")
                .append("LEFT JOIN SGE_DESCR_DEFEITO X on O.B_COD_DEFEITO = X.COD_DEFEITO ");
    }

    private void getColumns(List<GenericQueryItemDTO> columnsWithoutDescr, String type) {
        List<String> columnsNames = columnsWithoutDescr.stream().filter(c -> !c.isNoShow()).sorted(Comparator.comparing(GenericQueryItemDTO::getColumnSequence)).map(GenericQueryItemDTO::getColumnName).collect(toList());
        List<GenericQueryItemDTO> columns = columnsWithoutDescr;
        getDescriptionColumns(columns);
        if( !columnsNames.isEmpty()) {
            Map<String, List<Domain>> columnsSql = domainRepository.findDistinctByDescriptionInAndDomainID_Id(columnsNames, type).stream().sorted(Comparator.comparingInt(d ->
                    columns.stream().filter(c-> c.getColumnName().contains(d.getDescription())).findFirst().get().getColumnSequence().intValue())
            ).collect(groupingBy(Domain::getDescription, LinkedHashMap::new, toList()));

            mapColumns = columnsSql;

            //Ordena colunas com base na ordem submetida
            AtomicInteger i = new AtomicInteger(0);
            sql.append(columnsSql.values().stream().map(m -> m.get(0).getKey().concat( " as column_" + i.getAndIncrement())).collect(Collectors.joining(",")));
        }
    }

    private void getDescriptionColumns(List<GenericQueryItemDTO> columns) {
        int size = columns.size();
        int sequence = 0; // auxiliar para ajustar a ordem das colunas
        for (int i = 0; i < size ; i++) {
            columns.get(i).setColumnSequence(columns.get(i).getColumnSequence()+sequence);
            List<Domain> domains = domainRepository.findAllByDomainID_Id(columns.get(i).getColumnName());
            if(!domains.isEmpty() && !columns.get(i).isNoShow()){
                sequence++;
                Domain domain = domains.get(0);
                GenericQueryItemDTO genericQueryDTO = GenericQueryItemDTO.builder()
                        .flagOrder(columns.get(i).isFlagOrder()).columnName(domain.getDescription())
                        .columnSequence(columns.get(i).getColumnSequence()+1).build();
                columns.add(genericQueryDTO);
            }
        }
    }
}
