package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.view.UnityHistoricalView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface UnityHistoricalViewRepository extends JpaRepository<UnityHistoricalView, Long> {

    @Query( value = "select ROWNUM as id, LOG_COD_BARRAS, LOG_DATA, DE_SERIE, PARA_SERIE, DE_COD_SIT, PARA_COD_SIT, " +
            "DE_SIGLA_PROP, PARA_SIGLA_PROP, DE_COD_CENTRAL, PARA_COD_CENTRAL, DE_COD_ESTACAO, PARA_COD_ESTACAO, " +
            "DE_COD_ENDERECO, PARA_COD_ENDERECO, DE_COD_CAIXA, PARA_COD_CAIXA, DE_COD_PLACA, PARA_COD_PLACA, " +
            "DE_CGC_CPF_RESP, PARA_CGC_CPF_RESP, DE_SIGLA_LOCAL, PARA_SIGLA_LOCAL, DE_TECNICO, PARA_TECNICO, DE_PRAZO, " +
            "PARA_PRAZO, DE_PARA_LOCAL, PARA_PARA_LOCAL, DE_GARANTIA, PARA_GARANTIA, DE_VALOR, PARA_VALOR, DE_NUM_TP, " +
            "PARA_NUM_TP, DE_BASTIDOR, PARA_BASTIDOR, DE_CGC_CPF_INST, PARA_CGC_CPF_INST, DE_DOC_TRANSF, PARA_DOC_TRANSF, " +
            "DE_COD_BARRAS_PAI, PARA_COD_BARRAS_PAI, DE_DATA_CALIBR, PARA_DATA_CALIBR, DE_LOCACAO, PARA_LOCACAO, DE_DATA_SIT, " +
            "PARA_DATA_SIT, DE_TOMBAMENTO, PARA_TOMBAMENTO, DE_OBSERV, PARA_OBSERV from V_UN_HISTORICO " +
            " where (LOG_COD_BARRAS = :barcode or (:barcode is null or :barcode = '')) " +
            " and (DE_SIGLA_PROP =:fromResponsible or (:fromResponsible is null or :fromResponsible = '')) " +
            " and (DE_SIGLA_LOCAL =:fromDeposit or (:fromDeposit is null or :fromDeposit = '')) " +
            " and (DE_TECNICO =:fromTechnician or (:fromTechnician is null or :fromTechnician = '')) " +
            " ",
            countQuery = "select count(*) from V_UN_HISTORICO", nativeQuery = true)
    Page<UnityHistoricalView> find(String barcode,
                                   String fromResponsible, String fromDeposit, String fromTechnician, Pageable paging);


    @Query( value = "select ROWNUM as id, LOG_COD_BARRAS, LOG_DATA, DE_SERIE, PARA_SERIE, DE_COD_SIT, PARA_COD_SIT, " +
            "DE_SIGLA_PROP, PARA_SIGLA_PROP, DE_COD_CENTRAL, PARA_COD_CENTRAL, DE_COD_ESTACAO, PARA_COD_ESTACAO, " +
            "DE_COD_ENDERECO, PARA_COD_ENDERECO, DE_COD_CAIXA, PARA_COD_CAIXA, DE_COD_PLACA, PARA_COD_PLACA, " +
            "DE_CGC_CPF_RESP, PARA_CGC_CPF_RESP, DE_SIGLA_LOCAL, PARA_SIGLA_LOCAL, DE_TECNICO, PARA_TECNICO, DE_PRAZO, " +
            "PARA_PRAZO, DE_PARA_LOCAL, PARA_PARA_LOCAL, DE_GARANTIA, PARA_GARANTIA, DE_VALOR, PARA_VALOR, DE_NUM_TP, " +
            "PARA_NUM_TP, DE_BASTIDOR, PARA_BASTIDOR, DE_CGC_CPF_INST, PARA_CGC_CPF_INST, DE_DOC_TRANSF, PARA_DOC_TRANSF, " +
            "DE_COD_BARRAS_PAI, PARA_COD_BARRAS_PAI, DE_DATA_CALIBR, PARA_DATA_CALIBR, DE_LOCACAO, PARA_LOCACAO, DE_DATA_SIT, " +
            "PARA_DATA_SIT, DE_TOMBAMENTO, PARA_TOMBAMENTO, DE_OBSERV, PARA_OBSERV from V_UN_HISTORICO " +
            " where (LOG_COD_BARRAS = :barcode or (:barcode is null or :barcode = '')) " +
            " and (DE_SIGLA_PROP =:fromResponsible or (:fromResponsible is null or :fromResponsible = '')) " +
            " and (DE_SIGLA_LOCAL =:fromDeposit or (:fromDeposit is null or :fromDeposit = '')) " +
            " and (DE_TECNICO =:fromTechnician or (:fromTechnician is null or :fromTechnician = '')) " +
            " and ((LOG_DATA between :initialDate and :finalDate)) ",
            countQuery = "select count(*) from V_UN_HISTORICO", nativeQuery = true)
    Page<UnityHistoricalView> findWithDate(String barcode, LocalDateTime initialDate, LocalDateTime finalDate,
                                   String fromResponsible, String fromDeposit, String fromTechnician, Pageable paging);
}
