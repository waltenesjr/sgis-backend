package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.CableMovementFilterDTO;
import br.com.oi.sgis.dto.CableMovementQueryDTO;
import br.com.oi.sgis.entity.CableMovement;
import br.com.oi.sgis.entity.CableMovementID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CableMovementRepository extends JpaRepository<CableMovement, CableMovementID> {
    @Query("select cm from CableMovement cm where (upper(cm.id.unity.id) like %:search%" +
            " or upper(cm.id.unity.unityCode.id) like %:search% or upper(cm.id.unity.unityCode.description) like %:search% " +
            " or upper(cm.electricalProperty.id) like %:search% or upper(cm.componentMovType.id) like %:search% )" +
            " and (cm.date = :date  or (:date is null ))")
    Page<CableMovement> findLike(@Param("search") String search, @Param("date")LocalDateTime date, Pageable paging);

    @Query("select cm from CableMovement cm where cm.id.sequence = :sequence and cm.id.unity.id = :unityId")
    Optional<CableMovement> findById(@Param("sequence") Long sequence,@Param("unityId")  String unityId);

    @Query(value = "SELECT MAX(MD_SEQ) + 1 AS SEQ FROM MOV_DIVISIVEIS", nativeQuery = true)
    Long findLastId();

    @Query("select a from CableMovement a inner join a.id.unity b inner join b.unityCode c inner join b.unityCode d  " +
            " inner join a.electricalProperty e " +
            " where b.id =:#{#dto.barcode} and e.id = :#{#dto.propertyId} " +
            " and a.date between :#{#dto.initialDate} and :#{#dto.finalDate}")
    Page<CableMovement> findCableMovementWithDateFilter(@Param("dto") CableMovementQueryDTO dto, Pageable paging);

    @Query("select a from CableMovement a inner join a.id.unity b inner join b.unityCode c inner join b.unityCode d  " +
            " inner join a.electricalProperty e " +
            " where b.id =:#{#dto.barcode} and e.id = :#{#dto.propertyId} ")
    Page<CableMovement> findCableMovement(@Param("dto") CableMovementQueryDTO dto, Pageable paging);

    @Query("select a from CableMovement a inner join a.id.unity b inner join b.unityCode c inner join b.unityCode d  " +
            " inner join a.electricalProperty e " +
            " where (b.id =:#{#dto.barcode} or :#{#dto.barcode} is null )  and (e.id = :#{#dto.propertyId} or :#{#dto.propertyId} is null)" +
            " and (a.componentMovType.id = :#{#dto.movementType} or :#{#dto.movementType} is null)")
    Page<CableMovement> findLikeFilter(@Param("dto") CableMovementFilterDTO dto, Pageable paging);
}
