package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.view.GyreView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface GyreViewRepository extends JpaRepository<GyreView, Long> {

    @Query(value = "select count(rownum) as qtd from V_UN_GIRO2 where DE_COD_SIT != PARA_COD_SIT" +
            " AND PARA_COD_SIT = 'DIS' and PARA_COD_PLACA = :unityCode " +
            " and DATA between :initialPeriod and :finalPeriod", nativeQuery = true)
    Integer findEntranceByUnityCode(@Param("unityCode") String unityCode,@Param("initialPeriod") @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDateTime initialPeriod
            , @Param("finalPeriod") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime finalPeriod);

    @Query(value = "select count(rownum) as qtd from V_UN_GIRO2 where DE_COD_SIT != PARA_COD_SIT" +
            " AND DE_COD_SIT = 'DIS' and PARA_COD_PLACA = :unityCode " +
            " and DATA between :initialPeriod and :finalPeriod", nativeQuery = true)
    Integer findExitByUnityCode(@Param("unityCode") String unityCode, @Param("initialPeriod") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime initialPeriod
            , @Param("finalPeriod") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime finalPeriod);
}
