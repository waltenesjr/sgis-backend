package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.view.WarrantyView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WarrantyViewRepository extends JpaRepository<WarrantyView, Long> {

    @Query(value = "select  ROWNUM as id, COD_BARRAS, PRESTADOR, NUM_BR, CONTRATO, DOC_COMPRA, DATA_GARANTIA from V_GARANTIA where cod_barras =:unityId order by data_garantia desc", nativeQuery = true)
    List<WarrantyView> findByUnityId(@Param("unityId") String unityId);

    @Query(value = "select ROWNUM as id, COD_BARRAS, PRESTADOR, NUM_BR, CONTRATO, DOC_COMPRA, DATA_GARANTIA from V_GARANTIA where cod_barras =:unityId and ROWNUM <=1 order by data_garantia desc", nativeQuery = true)
    WarrantyView findOneByUnityId(@Param("unityId") String unityId);
}
