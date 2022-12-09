package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.RepairTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepairTicketRepository extends JpaRepository<RepairTicket, String>,  CostComparisonRepairRepositoryCustom{


    @Query(value = "select rt.B_NUM_BR from BILHETE rt order by rt.B_NUM_BR desc limit 1", nativeQuery = true)
    String findTop1ByIdDesc();

    @Query("select rt from RepairTicket rt where upper(rt.id) like %:search% " +
            "or upper(rt.unity.id) like %:search% or upper(rt.defect.id) like %:search% " +
            "or upper(rt.defect.description) like %:search%")
    Page<RepairTicket> findLike(@Param("search") String search, Pageable paging);

    Optional<RepairTicket> findTopByUnityIdAndSituationId(String unityId, String situationId);
    List<RepairTicket> findByUnityId(String unityId);

    @Query("select a from RepairTicket a inner join a.unity b inner join a.defect c inner join b.unityCode d " +
            "where a.situation.id in ('EGA', 'EGC', 'EGO', 'ERI', 'ERT', 'ECT', 'EOR', 'ACT')  and b.situationCode.id = 'REP' and a.repairCenterDepartment.id = :department " +
            " and (a.originDepartment.id like %:#{#dto.originId}%  or (:#{#dto.originId} is null or :#{#dto.originId}  = '' )) " +
            " and (d.id like %:#{#dto.unityId}%  or (:#{#dto.unityId} is null or :#{#dto.unityId}  = '' )) " +
            " and (b.id like %:#{#dto.barcode}%  or (:#{#dto.barcode} is null or :#{#dto.barcode}  = '' )) " +
            " and (a.situation.id like %:#{#dto.situation}%  or (:#{#dto.situation} is null or :#{#dto.situation}  = '' )) " +
            " and (a.repairTechnician.id like %:#{#dto.technicianId}%  or (:#{#dto.technicianId} is null or :#{#dto.technicianId}  = '' )) " +
            " and (a.contract.id like %:#{#dto.contractId}%  or (:#{#dto.contractId} is null or :#{#dto.contractId}  = '' )) " +
            " and (a.maintainer.id like %:#{#dto.maintainerId}%  or (:#{#dto.maintainerId} is null or :#{#dto.maintainerId}  = '' )) ")
    Page<RepairTicket> findTicketsToForward(@Param("dto") ForwardTicketDTO dto, @Param("department")String department, Pageable paging);

    @Query("select a from RepairTicket a inner join a.unity b inner join a.defect c inner join b.unityCode d " +
            "where a.situation.id in ('EGA', 'EGC', 'EGO', 'ERI', 'ERT', 'ECT', 'EOR', 'ACT') and b.situationCode.id = 'REP' and a.repairCenterDepartment.id = :department " +
            " and (a.originDepartment.id like %:#{#dto.originId}%  or (:#{#dto.originId} is null or :#{#dto.originId}  = '' )) " +
            " and (d.id like %:#{#dto.unityId}%  or (:#{#dto.unityId} is null or :#{#dto.unityId}  = '' )) " +
            " and (b.id like %:#{#dto.barcode}%  or (:#{#dto.barcode} is null or :#{#dto.barcode}  = '' )) " +
            " and (a.situation.id like %:#{#dto.situation}%  or (:#{#dto.situation} is null or :#{#dto.situation}  = '' )) " +
            " and (a.repairTechnician.id like %:#{#dto.technicianId}% or (:#{#dto.technicianId} is null or :#{#dto.technicianId}  = '' )) " +
            " and (a.contract.id like %:#{#dto.contractId}%  or (:#{#dto.contractId} is null or :#{#dto.contractId}  = '' )) " +
            " and (a.maintainer.id like %:#{#dto.maintainerId}%  or (:#{#dto.maintainerId} is null or :#{#dto.maintainerId}  = '' ))" +
            " and a.openDate between :#{#dto.initialDate} and :#{#dto.finalDate}")
    Page<RepairTicket> findTicketsToForwardWithDateFilter(@Param("dto") ForwardTicketDTO dto, @Param("department")String department, Pageable paging);

    @Modifying @Transactional
    @Query("update RepairTicket rt set rt.maintainer.id = :maintainerId, rt.repairTechnician.id = :technicianId, rt.contract.id = :contractId," +
            " rt.situation.id =:situation where rt.id = :brNumber  ")
    void forwardRepair(@Param("maintainerId")  String maintainerId, @Param("technicianId") String technicianId,
                       @Param("contractId")  String contractId, @Param("situation") String situation,@Param("brNumber")  String brNumber);

    @Query(value = "SELECT COUNT(IB_NUM_BR) AS TOTAL " +
            "FROM INTERVBILH WHERE IB_NUM_BR = :brNumber " +
            "AND IB_DATA_FIM IS NULL ", nativeQuery = true)
    Integer findForward(@Param("brNumber") String brNumber);

    @Query("select a from RepairTicket a inner join a.unity b inner join a.defect c inner join b.unityCode d inner join d.equipModelCode m " +
            " where a.situation.id in ('EGA', 'EGC', 'EGO', 'ERI', 'ERT', 'ECT', 'EOR', 'ACT')  and b.situationCode.id = 'REP' and a.repairCenterDepartment.id = :department " +
            " and (m.id like %:#{#dto.unityId}%  or (:#{#dto.unityId} is null or :#{#dto.unityId}  = '' )) " +
            " and (b.id like %:#{#dto.barcode}%  or (:#{#dto.barcode} is null or :#{#dto.barcode}  = '' )) " +
            " and (a.repairTechnician.id like %:#{#dto.technicianId}%  or (:#{#dto.technicianId} is null or :#{#dto.technicianId}  = '' )) " +
            " and ((:#{#dto.unitiesWithoutTechnician} is null or :#{#dto.unitiesWithoutTechnician} = false ) or (:#{#dto.unitiesWithoutTechnician} = true  and a.repairTechnician.id is null )) ")
    Page<RepairTicket> findDesignateTechnician(@Param("dto")DesignateTechnicianDTO designateTechnicianDTO, @Param("department") String department, Pageable paging);

    @Modifying @Transactional
    @Query("update RepairTicket rt set rt.repairTechnician.id = :technicianId " +
            " where rt.id = :brNumber  and rt.unity.id = :unityId ")
    void designateTechnician( @Param("technicianId") String technicianId, @Param("unityId") String unityId,@Param("brNumber") String brNumber);

    @Query("select rt from RepairTicket rt where rt.unity.situationCode.id = 'REP' and " +
            "upper(rt.repairCenterDepartment.id) like %:departmentId% and " +
            "(select max(ti.id.sequence) from TicketIntervention ti where ti.id.repairTicket.id = rt.id) is null " +
            "and (upper(rt.id) like %:search% " +
            "or upper(rt.unity.id) like %:search%  or upper(rt.unity.barcodeParent) like %:search% " +
            "or upper(rt.defect.id) like %:search% " +
            "or upper(rt.defect.description) like %:search% )")
    Page<RepairTicket> findTicketsForInterventionLike(@Param("search") String search, @Param("departmentId")String departmentId, Pageable paging);

    @Modifying @Transactional
    @Query("update RepairTicket rt set rt.situation.id = :situation where rt.id = :brNumber")
    void updateSituation(@Param("brNumber")  String brNumber,@Param("situation") String situation);

    @Query("select new br.com.oi.sgis.dto.RepairSummaryQuantityDTO(month(rt.closureDate), year(rt.closureDate), max(rt.repairCenterDepartment.id) , " +
            " sum(case when rt.situation.id='CON' then 1 else 0 end)," +
            " sum(case when rt.situation.id='SCR' then 1 else 0 end), " +
            " sum(case when rt.situation.id='SDE' then 1 else 0 end)," +
            " sum(case when rt.situation.id='AJU' then 1 else 0 end)," +
            " sum(case when rt.situation.id='GAR' then 1 else 0 end)," +
            " sum(case when rt.situation.id='ORC' then 1 else 0 end)," +
            " sum(case when rt.situation.id='CTR' then 1 else 0 end)," +
            " sum(case when rt.situation.id='GCT' then 1 else 0 end)," +
            " sum(case when rt.situation.id='GOR' then 1 else 0 end)," +
            " sum(case when rt.situation.id='SCE' then 1 else 0 end), 0) " +
            " from RepairTicket rt where rt.closureDate is not null  " +
            " and (rt.repairCenterDepartment.id like %:repairCenter%  or (:repairCenter is null or :repairCenter  = '' ))" +
            " and ((:initialDate is null and :finalDate is null) or (rt.closureDate between :initialDate and :finalDate)) " +
            " group by year(rt.closureDate), month(rt.closureDate) order by year(rt.closureDate), month(rt.closureDate)")
    List<RepairSummaryQuantityDTO> getQuantitySummary(String repairCenter, LocalDateTime initialDate, LocalDateTime finalDate);


    @Query("select new br.com.oi.sgis.dto.RepairSummaryValueDTO(month(rt.closureDate), year(rt.closureDate), max(rt.repairCenterDepartment.id), " +
            " sum(case when rt.situation.id='CON' then rt.repairValue when rt.repairValue is null then 0 else 0 end)," +
            " sum(case when rt.situation.id='SCR' then rt.repairValue when rt.repairValue is null then 0 else 0 end), " +
            " sum(case when rt.situation.id='SDE' then rt.repairValue when rt.repairValue is null then 0 else 0 end)," +
            " sum(case when rt.situation.id='AJU' then rt.repairValue when rt.repairValue is null then 0 else 0 end)," +
            " sum(case when rt.situation.id='GAR' then rt.repairValue when rt.repairValue is null then 0 else 0 end)," +
            " sum(case when rt.situation.id='ORC' then rt.repairValue when rt.repairValue is null then 0 else 0 end)," +
            " sum(case when rt.situation.id='CTR' then rt.repairValue when rt.repairValue is null then 0 else 0 end)," +
            " sum(case when rt.situation.id='GCT' then rt.repairValue when rt.repairValue is null then 0 else 0 end)," +
            " sum(case when rt.situation.id='GOR' then rt.repairValue when rt.repairValue is null then 0 else 0 end)," +
            " sum(case when rt.situation.id='SCE' then rt.repairValue when rt.repairValue is null then 0 else 0 end), 0) from RepairTicket rt where rt.closureDate is not null " +
            "  and (rt.repairCenterDepartment.id like %:repairCenter%  or (:repairCenter is null or :repairCenter  = '' )) " +
            " and ((:initialDate is null and :finalDate is null) or (rt.closureDate between :initialDate and :finalDate)) " +
            "group by year(rt.closureDate), month(rt.closureDate) order by year(rt.closureDate), month(rt.closureDate)")
    List<RepairSummaryValueDTO> getValuesSummary(String repairCenter, LocalDateTime initialDate, LocalDateTime finalDate);


    @Query("select count(*) as total from RepairTicket rt2 where month(rt2.closureDate) = :month and year(rt2.closureDate) = :year ")
    int openTicketsByMonthAndYear(int month, int year);

    @Query("select new br.com.oi.sgis.dto.AnalyticRepairDTO(rt.id, un.id, rt.openDate, ea.id, ea.description,ea.id, rt.originDepartment.id, " +
            " rt.repairCenterDepartment.id, d.id, d.description, rt.situation.id, rt.repairValue, w.warrantyDate, w.contract, ea.equipModelCode.id ) from RepairTicket rt, WarrantyView w, Company  c left join rt.unity un" +
            " left join un.unityCode ea left join rt.defect d where w.provider = c.id and un.id = w.unityId  " +
            " and (ea.id like %:#{#dto.unityCode}%  or (:#{#dto.unityCode} is null or :#{#dto.unityCode}  = '' ))" +
            " and (rt.repairCenterDepartment.id like %:#{#dto.repairCenter}%  or (:#{#dto.repairCenter} is null or :#{#dto.repairCenter}  = '' ))" +
            " and (rt.situation.id like %:#{#dto.situation}%  or (:#{#dto.situation} is null or :#{#dto.situation}  = '' ))" +
            " and (rt.originDepartment.id like %:#{#dto.originDepartment}%  or (:#{#dto.originDepartment} is null or :#{#dto.originDepartment}  = '' ))" +
            " and (d.id like %:#{#dto.defect}%  or (:#{#dto.defect} is null or :#{#dto.defect}  = '' ))" +
            " and (:#{#dto.initialRepairDate} is null  or (rt.openDate between :#{#dto.initialRepairDate} and :#{#dto.finalRepairDate})) " +
            " and (:#{#dto.initialWarrantyDate} is null  or (w.warrantyDate between :#{#dto.initialWarrantyDate} and :#{#dto.finalWarrantyDate}))")
    List<AnalyticRepairDTO> getAnalytics(@Param("dto")RepairAnalyticFilterDTO dto);

    @Query("select (case when ti.externalRepair=false then 'INTERNO' when ti.externalRepair=true then concat('EXTERNO - ', max(ie.id.estimate.id)) else 'NENHUM' end) " +
            " from TicketIntervention ti, ItemEstimate ie where ti.id.repairTicket.id = :brNumber group by ti.id.repairTicket.id, ti.externalRepair")
    String externalInternalRepair(String brNumber);

    @Query("select new br.com.oi.sgis.dto.ExternalRepairReportDTO(rp.id, un.id,concat(un.id, '\n', ea.id, '\n', ea.description), " +
            " rp.repairCenterDepartment.id, rp.originDepartment.id, rp.openDate, concat(c.id, ' - ', c.companyName), e.id, e.date, e.sendDate, " +
            " d.id, d.description, ie.approvalDate, ie.cancelDate, ie.returnDate, e.deliveryDays, rp.situation.id)" +
            " from ItemEstimate ie left join ie.id.estimate e " +
            " left join ie.id.ticketIntervention ti left join ti.id.repairTicket rp left join rp.defect d left join rp.unity un" +
            " left join un.unityCode ea left join e.company c where ti.externalRepair = true " +
            " and (ea.id like %:#{#dto.unityCode}%  or (:#{#dto.unityCode} is null or :#{#dto.unityCode}  = '' )) " +
            " and (un.id like %:#{#dto.barcode}%  or (:#{#dto.barcode} is null or :#{#dto.barcode}  = '' )) " +
            " and (rp.repairCenterDepartment.id like %:#{#dto.repairCenter}%  or (:#{#dto.repairCenter} is null or :#{#dto.repairCenter}  = '' ))" +
            " and (rp.originDepartment.id like %:#{#dto.originDepartment}%  or (:#{#dto.originDepartment} is null or :#{#dto.originDepartment}  = '' ))" +
            " and (d.id like %:#{#dto.defect}%  or (:#{#dto.defect} is null or :#{#dto.defect}  = '' ))" +
            " and (c.id like %:#{#dto.provider}%  or (:#{#dto.provider} is null or :#{#dto.provider}  = '' ))" +
            " and (rp.situation.id like %:#{#dto.situation}%  or (:#{#dto.situation} is null or :#{#dto.situation}  = '' ))" +
            " and (:#{#dto.initialRepairDate} is null  or (rp.openDate between :#{#dto.initialRepairDate} and :#{#dto.finalRepairDate})) " +
            " and (:#{#dto.initialWarrantyDate} is null  or (un.warrantyDate between :#{#dto.initialWarrantyDate} and :#{#dto.finalWarrantyDate})) " +
            " and (:#{#dto.initialEstimateDate} is null  or (e.date between :#{#dto.initialEstimateDate} and :#{#dto.finalEstimateDate})) " +
            " and (:#{#dto.initialExitDate} is null  or (e.sendDate between :#{#dto.initialExitDate} and :#{#dto.finalExitDate})) " +
            " and (:#{#dto.initialAcceptanceDate} is null  or (e.approvalDate between :#{#dto.initialAcceptanceDate} and :#{#dto.finalAcceptanceDate})) " +
            " and (:#{#dto.initialArriveDate} is null  or (e.returnDate between :#{#dto.initialArriveDate} and :#{#dto.finalArriveDate})) " +
            " and ((e.cancelDate is not null and :#{#dto.cancels} = false) or (:#{#dto.cancels} = true) )")
    List<ExternalRepairReportDTO> getExternalRepair(ExternalRepairFilterDTO dto);

    @Query("select new br.com.oi.sgis.dto.OperatorTicketDTO(rt.id, rt.openDate, rt.situation.id, d.id, d.description, rt.repairCenterDepartment.id," +
            " p.id, p.technicianName, e.id, e.companyName, e.tradeName) from RepairTicket rt inner join rt.technician p" +
            " inner join p.cgcCpfCompany.company e inner join rt.defect d where  " +
            " (rt.repairCenterDepartment.id like %:#{#dto.repairCenter}%  or (:#{#dto.repairCenter} is null or :#{#dto.repairCenter}  = '' ))" +
            " and (rt.situation.id like %:#{#dto.situation}%  or (:#{#dto.situation} is null or :#{#dto.situation}  = '' ))" +
            " and (d.id like %:#{#dto.defect}%  or (:#{#dto.defect} is null or :#{#dto.defect}  = '' ))" +
            " and (e.id like %:#{#dto.operator}%  or (:#{#dto.operator} is null or :#{#dto.operator}  = '' ))" +
            " and (:#{#dto.initialRepairDate} is null  or (rt.openDate between :#{#dto.initialRepairDate} and :#{#dto.finalRepairDate})) order by e.companyName, p.technicianName")
    List<OperatorTicketDTO> getOperatorTicket(OperatorTicketFilterDTO dto);


    @Query("select new br.com.oi.sgis.dto.TicketReleasedDTO(rt.id, rt.openDate, u.id, ea.id, ea.description, ea.mnemonic, rt.devolutionDepartment.id," +
            " rt.originDepartment.id, rt.closureDate, rt.devolutionDate, rt.repairValue) from RepairTicket rt inner join rt.unity u" +
            " inner join u.unityCode ea where  " +
            " (rt.repairCenterDepartment.id like %:#{#dto.repairCenter}%  or (:#{#dto.repairCenter} is null or :#{#dto.repairCenter}  = '' ))" +
            " and (u.situationCode.id = :#{#dto.condition}  or (:#{#dto.condition} is null or :#{#dto.condition}  = '' ))" +
            " and (rt.originDepartment.id like %:#{#dto.origin}%  or (:#{#dto.origin} is null or :#{#dto.origin}  = '' ))" +
            " and (rt.devolutionDepartment.id like %:#{#dto.devolution}%  or (:#{#dto.devolution} is null or :#{#dto.devolution}  = '' ))" +
            " and (:#{#dto.initialOpenDate} is null  or (rt.openDate >= :#{#dto.initialOpenDate} and rt.openDate <= :#{#dto.finalOpenDate}))" +
            " and (:#{#dto.initialCloseDate} is null  or (rt.closureDate >= :#{#dto.initialCloseDate} and rt.closureDate <= :#{#dto.finalCloseDate})) " +
            " order by rt.openDate ")
    List<TicketReleasedDTO> getTicketReleased(TicketReleasedFilterDTO dto);

    @Query("select new br.com.oi.sgis.dto.TicketForwardedDTO(sr.id, sr.description, rt.repairCenterDepartment.id, " +
            " u.id, rt.id, rt.openDate, rt.devolutionDepartment.id, rt.originDepartment.id, ea.id, ea.description, ea.mnemonic) from RepairTicket rt inner join rt.unity u " +
            " inner join u.unityCode ea inner join rt.defect d  inner join rt.situation sr where " +
            " (sr.id in (:situations) or (:#{#dto.condition} is null) )" +
            " and (rt.repairCenterDepartment.id like %:#{#dto.repairCenter}%  or (:#{#dto.repairCenter} is null or :#{#dto.repairCenter}  = '' ))" +
            " and (rt.originDepartment.id like %:#{#dto.origin}%  or (:#{#dto.origin} is null or :#{#dto.origin}  = '' ))" +
            " and (rt.devolutionDepartment.id like %:#{#dto.devolution}%  or (:#{#dto.devolution} is null or :#{#dto.devolution}  = '' ))" +
            " and (:#{#dto.initialDate} is null  or (rt.openDate >= :#{#dto.initialDate} and rt.openDate <= :#{#dto.finalDate}))" +
            " order by sr.id, rt.openDate ")
    List<TicketForwardedDTO> getTicketForwarded(TicketForwardedFilterDTO dto, List<String> situations);

    @Query("select new br.com.oi.sgis.dto.AverageTimeDTO(rt.id, rt.openDate, rt.situation.id, u.id, ea.id, ea.description, rt.repairCenterDepartment.id, " +
            " rt.acceptDate, rt.closureDate) from RepairTicket rt inner join rt.unity u " +
            " inner join u.unityCode ea where rt.closureDate is not null" +
            " and (rt.repairCenterDepartment.id like %:#{#dto.repairCenter}%  or (:#{#dto.repairCenter} is null or :#{#dto.repairCenter}  = '' ))" +
            " and (ea.id like %:#{#dto.unity}%  or (:#{#dto.unity} is null or :#{#dto.unity}  = '' ))" +
            " and (:#{#dto.initialDate} is null  or (rt.acceptDate >= :#{#dto.initialDate})) " +
            " and (:#{#dto.finalDate} is null  or (rt.closureDate <= :#{#dto.finalDate})) ")
    List<AverageTimeDTO> getAverageTime(AverageTimeFilterDTO dto);

    @Query("select new br.com.oi.sgis.dto.OpenRepairDTO(rt.id, rt.openDate, rt.prevision, u.id, coalesce(rt.acceptDate, rt.openDate), rt.situation.id, " +
            " (case when ti.externalRepair is null then '?' when ti.externalRepair = true then 'Externo' else 'Interno' end ), e.id, e.sendDate, c.companyName, rt.repairCenterDepartment.id," +
            " ea.id, ea.mnemonic, ea.description, i.id, i.description, ti.observation ) from RepairTicket rt inner join rt.unity u " +
            " inner join u.unityCode ea left join TicketIntervention ti on ti.id.repairTicket.id = rt.id " +
            " left join ti.intervention i left join ItemEstimate ie on ie.id.ticketIntervention.id.sequence = ti.id.sequence and ie.id.ticketIntervention.id.repairTicket.id = ti.id.repairTicket.id " +
            " left join ie.id.estimate e left join e.company c" +
            " where rt.situation.id in ('ABE', 'ACT', 'ERI','EGO', 'EGC', 'EGA', 'ECT', 'EOR') " +
            " and ti.finalDate is null " +
            " and (rt.repairCenterDepartment.id like %:#{#dto.repairCenter}%  or (:#{#dto.repairCenter} is null or :#{#dto.repairCenter}  = '' ))" +
            " and (rt.originDepartment.id like %:#{#dto.origin}%  or (:#{#dto.origin} is null or :#{#dto.origin}  = '' ))" +
            " and (rt.devolutionDepartment.id like %:#{#dto.devolution}%  or (:#{#dto.devolution} is null or :#{#dto.devolution}  = '' ))" +
            " and (:#{#dto.initialDate} is null  or (rt.openDate between :#{#dto.initialDate} and :#{#dto.finalDate})) " +
            " and ((:#{#dto.notForwarded} = false or ti.externalRepair is null) " +
            " or (:#{#dto.internalRepair} = false or ti.externalRepair = false) " +
            " or (:#{#dto.externalRepair} = false or ti.externalRepair = true) )" +
            " order by rt.repairCenterDepartment.id, rt.openDate")
    List<OpenRepairDTO> getOpenRepairs(OpenRepairFilterDTO dto);
}
