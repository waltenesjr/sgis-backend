package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.SapOperationHistoryDTO;
import br.com.oi.sgis.dto.SapOperationHistoryReportDTO;
import br.com.oi.sgis.entity.InformaticsRec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InformaticsRecRepository extends JpaRepository<InformaticsRec, String> {

    @Query("select new br.com.oi.sgis.dto.SapOperationHistoryReportDTO(r.operation, r.unity.id, r.processingDate, r.messageCode, r.message, d.description) " +
            "from InformaticsRec r, InformaticsEnv e, Domain d " +
            "where r.id = e.id " +
            "and coalesce(r.canBeReprocessed, 0) = coalesce(d.key,0) " +
            "and d.domainID.id = 'PODE_REPROCESSAR' "  +
            "and (e.spareCenterId like :#{#dto.spareCenterId} or (:#{#dto.spareCenterId} is null or :#{#dto.spareCenterId} = '' )) "  +
            "and (r.unity.id like :#{#dto.unityId} or (:#{#dto.unityId} is null or :#{#dto.unityId}  = '' )) " +
            "and r.processingDate between :#{#dto.initialDate} and :#{#dto.finalDate} "  +
            "and (substring(r.messageCode,1,1) = :#{#dto.situation} or (:#{#dto.situation} is null or :#{#dto.situation} = '' ))"  +
            "and ( upper(e.orderNumber) like %:#{#dto.search}% or upper(e.fixedNumber) like %:#{#dto.search}% or upper(e.reservationNumber) like %:#{#dto.search}%) ")
    Page<SapOperationHistoryReportDTO> findSapOperationHistory(@Param("dto") SapOperationHistoryDTO sapOperationHistoryDTO, Pageable pageable);
}
