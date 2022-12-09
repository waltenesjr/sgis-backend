package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.TicketIntervention;
import br.com.oi.sgis.entity.TicketInterventionID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketInterventionRepository extends JpaRepository<TicketIntervention, TicketInterventionID> {

    @Query(value = "select new br.com.oi.sgis.dto.TicketIntervExtraInfoDTO(ti.id,ti.externalRepair, ti.repSituation.id, ti.initialDate, ti.finalDate, i.id, " +
            "i.description, t.id, t.technicianName, u.id, uc.description, e.id, c.companyName, ti.cpValue, ti.laborValue) from TicketIntervention ti, ItemEstimate ie " +
            "left join ti.intervention i left join ti.technician t " +
            "left join ti.unity u left join u.unityCode uc " +
            "left join ie.id.estimate e left join e.company c where ie.id.ticketIntervention = ti.id and ti.id.repairTicket.id = :ticketNumber")
    List<TicketIntervExtraInfoDTO> findByRepairTicket(@Param("ticketNumber") String ticketNumber);

    @Query("select ti from TicketIntervention ti where upper(ti.unity.id) like %:search% or upper(ti.unity.unityCode.id) like %:search% or " +
            " upper(ti.unity.unityCode.description) like %:search% or upper(ti.id.repairTicket.id) like %:search% or upper(ti.id.sequence) like %:search% or" +
            " upper(ti.technician.id) like %:search% or upper(ti.technician.technicianName) like %:search% or upper(ti.intervention.id) like %:search% " +
            " or upper(ti.intervention.description) like %:search%" )
    Page<TicketIntervention> findLike(@Param("search") String search, Pageable paging);

    @Query("select ti from TicketIntervention ti where ti.finalDate is null and ti.externalRepair = false and (upper(ti.unity.id) like %:search% or upper(ti.unity.unityCode.id) like %:search% or " +
            " upper(ti.unity.unityCode.description) like %:search% or upper(ti.id.repairTicket.id) like %:search% or upper(ti.id.sequence) like %:search% or" +
            " upper(ti.technician.id) like %:search% or upper(ti.technician.technicianName) like %:search% or upper(ti.intervention.id) like %:search% " +
            " or upper(ti.intervention.description) like %:search% )" )
    Page<TicketIntervention> findLikeToClose(@Param("search") String search, Pageable paging);

    @Query("select ti from TicketIntervention ti where ti.id.repairTicket.id =:brNumber and ti.externalRepair = false and " +
            " ti.unity.situationCode.id = 'REP' and upper(ti.id.repairTicket.repairCenterDepartment.id) like %:departmentId% and " +
            " (upper(ti.unity.id) like %:search% or upper(ti.unity.unityCode.id) like %:search% or " +
            " upper(ti.unity.unityCode.description) like %:search% or upper(ti.id.repairTicket.id) like %:search% or upper(ti.id.sequence) like %:search% or" +
            " upper(ti.technician.id) like %:search% or upper(ti.technician.technicianName) like %:search% or upper(ti.intervention.id) like %:search% " +
            " or upper(ti.intervention.description) like %:search% )" )
    Page<TicketIntervention> findLikeToUpdate(@Param("search") String search,  @Param("departmentId")String departmentId,  @Param("brNumber")String brNumber, Pageable paging);

    @Query("select ti from TicketIntervention ti where ti.id.sequence = :sequence and ti.id.repairTicket.id = :brNumber")
    Optional<TicketIntervention> findById(@Param("brNumber") String brNumber, @Param("sequence") Long sequence);

    @Query(value = "SELECT DECODE(MAX(IB_SEQ),NULL,0,MAX(IB_SEQ))+1 AS SEQ FROM INTERVBILH", nativeQuery = true)
    Long getNextSequence();

    @Query("select ti from TicketIntervention ti where ti.unity.id = :barcode and ti.id.repairTicket.id = :brNumber")
    Optional<TicketIntervention>findByUnityAndTicket(@Param("brNumber") String brNumber, @Param("barcode") String barcode);

    @Query("select new br.com.oi.sgis.dto.TechnicianTicketDTO(b.id, p.id, p.technicianName, e.id, e.description, u.id, u.responsible.id, b.repairCenterDepartment.id, s.id, s.description,b.openDate ) " +
            " from RepairTicket b inner join b.unity u inner join u.unityCode e  inner join b.repairTechnician p inner join b.defect s " +
            " where b.situation.id in ('ACT', 'ERI') and (p.id =:idTechnician or (:#{#idTechnician} = '' and  b.repairCenterDepartment.id = :department))" )
    List<TechnicianTicketDTO> getTechnicianData(@Param("idTechnician") String idTechnician, @Param("department") String department);

    @Query("select new br.com.oi.sgis.dto.OrderServiceDTO(b.id, p.id, p.technicianName,  u.id, u.barcodeParent, e.id, e.description, u.deposit.id, s.description, b.description) " +
            " from RepairTicket b inner join b.unity u inner join u.unityCode e  left join b.repairTechnician p inner join b.defect s " +
            " where (b.cancelDate is null and b.closureDate is null) " +
            " and (p.id =:idTechnician or ( :#{#idTechnician} is null or :#{#idTechnician} = '' )) " +
            " and (u.id =:barcode or (:#{#barcode} is null or :#{#barcode} = '') )" +
            " and (u.deposit.id =:department or (:#{#department} is null or :#{#department} = '') ) " )
    List<OrderServiceDTO> getOrderServiceData(@Param("idTechnician")String idTechnician, @Param("barcode")String barcode, @Param("department")String department);


    @Modifying
    @Query("update TicketIntervention ti set ti.finalDate =:closeDate where ti.id.sequence =:sequence and ti.id.repairTicket.id = :brNumber")
    void closeIntervention(@Param("sequence")Long sequence,@Param("brNumber") String brNumber, @Param("closeDate") LocalDateTime closeDate);

    @Modifying
    @Query("update TicketIntervention ti set ti.intervention.id =:itervention, ti.operator.id =:technicianId, ti.technician.id = :technicianId,  " +
            " ti.observation =:obs where ti.id.sequence =:sequence and ti.id.repairTicket.id = :brNumber")
    void update(@Param("sequence")Long sequence,@Param("brNumber") String brNumber, @Param("itervention") String intervention,  @Param("technicianId") String technicianId, @Param("obs") String obs);


    @Query("select new br.com.oi.sgis.dto.TechnicianTimesDTO(p.id, u.id, p.technicianName, p.departmentCode.id, e.equipModelCode.id, e.id, e.description, " +
            " i.initialDate,coalesce(i.finalDate, i.initialDate), v.id, v.description ) " +
            " from TicketIntervention i inner join i.id.repairTicket b inner join i.technician p  inner join b.unity u inner join u.unityCode e inner" +
            " join i.intervention v where i.externalRepair = false and v.productiveFlag = true " +
            " and (p.id =:#{#filterDTO.technician} or (:#{#filterDTO.technician} is null or :#{#filterDTO.technician} = ''))" +
            " and (p.departmentCode.id =:#{#filterDTO.department} or (:#{#filterDTO.department} is null or :#{#filterDTO.department} = ''))" +
            " and (e.equipModelCode.id =:#{#filterDTO.modelEquipment} or (:#{#filterDTO.modelEquipment} is null or :#{#filterDTO.modelEquipment} = ''))" +
            " and (i.initialDate>=:#{#filterDTO.initialDate} or (:#{#filterDTO.initialDate} is null)) " +
            " and (i.finalDate<=:#{#filterDTO.finalDate} or (:#{#filterDTO.finalDate} is null)) " )
    List<TechnicianTimesDTO> findTechnicianTimes(@Param("filterDTO")TechnicianTimesFilterDTO filterDTO);
}
