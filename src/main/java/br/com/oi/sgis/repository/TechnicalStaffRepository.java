package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.EmitProofDTO;
import br.com.oi.sgis.dto.EmitProofReportDTO;
import br.com.oi.sgis.entity.TechnicalStaff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TechnicalStaffRepository extends JpaRepository<TechnicalStaff, String> {

    @Query("select ts from TechnicalStaff ts left join ts.departmentCode d " +
            " where upper(ts.id) like %:search% " +
            " or upper(ts.technicianName) like %:search% " +
            " or trim(ts.techPhoneBase) like %:#{[0].trim()}% " +
            " or trim(ts.techPhoneResid) like %:#{[0].trim()}%  " +
            " or upper(ts.email) like %:search% or upper(ts.departmentCode.description)  like %:search% " +
            " or upper(ts.cgcCpfCompany.company.companyName)  like %:search% " +
            " or upper(ts.managerName) like %:search% " +
            " or upper(d.id) like %:search% or upper(d.description) like %:search%")
    Page<TechnicalStaff> findLike(@Param("search") String search, Pageable paging);

    @Modifying @Transactional
    @Query("update TechnicalStaff  ts set ts.manHourValue = :manHourValue where ts.id = :id")
    void updateManHour(@Param("id") String id,@Param("manHourValue") BigDecimal manHourValue);

    @Query("select ts from TechnicalStaff ts left join ts.departmentCode d " +
            " where ts.repairFlag = true and ts.departmentCode.id = :department and ts.active = true " +
            " and (upper(ts.id) like %:search% " +
            " or upper(ts.technicianName) like %:search% " +
            " or trim(ts.techPhoneBase) like %:#{[0].trim()}% " +
            " or trim(ts.techPhoneResid) like %:#{[0].trim()}%  " +
            " or upper(ts.email) like %:search% " +
            " or upper(ts.managerName) like %:search% " +
            " or upper(d.id) like %:search% or upper(d.description) like %:search%)")
    Page<TechnicalStaff> findToForwardTicket(@Param("search") String search, @Param("department") String department, Pageable paging);

    @Query("select new br.com.oi.sgis.dto.EmitProofReportDTO(a.id, a.technicianName, a.departmentCode.id, e.id, e.description, u.id, u.responsible.id, u.situationDateChange) from Unity u inner join u.technician a inner join u.unityCode e " +
            " where u.technician.id = :#{#emitProofDTO.technicianId} and u.situationCode.id in ('USO', 'EMU') " +
            " and u.barcodeParent is null and u.situationDateChange between :#{#emitProofDTO.initialDate} and :#{#emitProofDTO.finalDate} " +
            " and u.responsible.id like %:#{#emitProofDTO.responsibleId} order by a.id, a.technicianName, e.id")
    List<EmitProofReportDTO> emitProof(EmitProofDTO emitProofDTO);

    @Query("select ts from TechnicalStaff ts left join ts.departmentCode d " +
            " where d.id = :department and ts.active = true and (upper(ts.id) like %:search% " +
            " or upper(ts.technicianName) like %:search% " +
            " or trim(ts.techPhoneBase) like %:#{[0].trim()}% " +
            " or trim(ts.techPhoneResid) like %:#{[0].trim()}%  " +
            " or upper(ts.email) like %:search% " +
            " or upper(ts.managerName) like %:search% " +
            " or upper(d.id) like %:search% or upper(d.description) like %:search%)")
    Page<TechnicalStaff> findLikeByUnity(@Param("search") String search,@Param("department") String department, Pageable paging);
}
