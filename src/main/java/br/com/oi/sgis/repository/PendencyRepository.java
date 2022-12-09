package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Pendency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface PendencyRepository extends JpaRepository<Pendency, String> {

    @Modifying @Transactional
    @Query(value= "insert into PENDENCIAS(PN_COD_PLACA, PN_COD_BARRAS, PN_MOV_ORIGEM, PN_SIGLA_LOCAL,PN_PARA_LOCAL," +
            "PN_TECNICO, PN_OPERADOR,PN_DATA, PN_OBS ) values (:id, :unityId, :originMov,:departmentOrigin, :departmentDestination," +
            " :technician,:operator,:initialDate, :observation ) ", nativeQuery = true )
    int insertNewPendency(@Param("id") String id, @Param("unityId") String unityId,@Param("originMov") String originMov,
                           @Param("departmentOrigin") String departmentOrigin, @Param("departmentDestination") String departmentDestination,
                           @Param("technician") String technician, @Param("operator") String operator, @Param("observation") String observation,
                           @Param("initialDate") LocalDateTime initialDate);

}
