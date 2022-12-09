package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.EstimateItemReportDTO;
import br.com.oi.sgis.dto.ItemEstimatesAnalysisDTO;
import br.com.oi.sgis.entity.ItemEstimate;
import br.com.oi.sgis.entity.ItemEstimateID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemEstimateRepository extends JpaRepository<ItemEstimate, ItemEstimateID> {

    @Query("select new br.com.oi.sgis.dto.EstimateItemReportDTO(i.id.estimate.id, d.id.sequence, i.value, r.id, d.repSituation.id, " +
            " b.id, u.id, e.id, p.costCenter, t.costCenter, e.description, p.id, e.mnemonic, s.technique.id, d.intervention.id, d.intervention.description, " +
            " d.technician.id, d.observation, d.operator.id, b.description, f.id, f.description, b.openDate, i.provider, u.warrantyDate," +
            " e.weight, e.value, i.approvalDate, i.cancelDate, u.serieNumber, e.fiscalClassification, r.description ) from " +
            " ItemEstimate i inner join i.id.ticketIntervention.id.repairTicket b left join i.id.ticketIntervention d " +
            " left join d.unity u inner join u.unityCode e inner join b.originDepartment p " +
            " inner join b.defect f inner join e.equipModelCode t inner join t.equipamentType s " +
            " inner join b.situation r where i.id.estimate.id = :estimateNumber")
    List<EstimateItemReportDTO> estimateItemReport(@Param("estimateNumber") String estimateNumber);

    @Modifying
    @Query("update ItemEstimate ie set ie.value =:value, ie.provider =:provider where ie.id.ticketIntervention.id.sequence = :sequence and" +
            " ie.id.ticketIntervention.id.repairTicket.id =:brNumber and ie.id.estimate.id =:estimate")
    void update(@Param("value") BigDecimal value, @Param("provider") String provider, @Param("sequence") Long sequence,
                @Param("brNumber") String brNumber, @Param("estimate") String estimate);
    List<ItemEstimate> findAllByIdEstimateId(String estimateId);

    @Query("SELECT ie from ItemEstimate ie where ie.approvalDate is null and ie.cancelDate is null and ie.id.estimate.department.id = :department " +
            " and ie.id.ticketIntervention.finalDate is null" +
            " and ((ie.id.ticketIntervention.unity.id like %:#{#dto.barcode}%  or (:#{#dto.barcode} is null or :#{#dto.barcode}  = '' )) " +
            " and (ie.id.estimate.id like %:#{#dto.estimateId}%  or (:#{#dto.estimateId} is null or :#{#dto.estimateId}  = '' )) " +
            " and ((:#{#dto.initialDate} is null) or (ie.id.ticketIntervention.id.repairTicket.openDate between :#{#dto.initialDate} and :#{#dto.finalDate})))")
    Page<ItemEstimate> findLike(@Param("dto") ItemEstimatesAnalysisDTO dto, @Param("department")String department, Pageable paging);

    @Query("select ie from ItemEstimate ie where ie.id.ticketIntervention.id.sequence =:sequence and ie.id.ticketIntervention.id.repairTicket.id =:brNumber " +
            " and ie.id.estimate.id =:estimateId")
    Optional<ItemEstimate> findById(@Param("brNumber") String brNumber, @Param("sequence") Long sequence, @Param("estimateId") String estimateId);

    @Modifying
    @Query("update ItemEstimate ie set ie.analyzed =:analyzed  where ie.id.ticketIntervention.id.sequence =:sequence and ie.id.ticketIntervention.id.repairTicket.id =:brNumber " +
            " and ie.id.estimate.id =:estimateId")
    void updateAnalysis( @Param("estimateId") String estimateId, @Param("brNumber")  String brNumber, @Param("sequence") Long sequence, @Param("analyzed") boolean analyzed);
}
