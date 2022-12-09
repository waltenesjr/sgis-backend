package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, String> {
    @Query("select d from Department d left join d.manager m left join d.contact c " +
            "where upper(d.id) like  %:search% or upper(d.description) like %:search% " +
            "or upper(m.technicianName) like  %:search% or upper(m.id) like %:search% " +
            "or upper(c.technicianName) like  %:search% or upper(c.id)like %:search% " +
            "or upper(d.repairCenterDefault) like  %:search% or d.phone like  %:search% ")
    Page<Department> findLike(@Param("search") String search, Pageable pageable);

    @Query("select d from RepairTicket rt left join rt.devolutionDepartment d where rt.openDate = " +
            "( select max(b.openDate) from RepairTicket b where b.unity.id = :barcode)")
    Optional<Department> findDevolutionDepMostRecent(@Param("barcode") String barcode);

    @Query("select d from Department d left join d.manager m left join d.contact c " +
            "where d.id in (select distinct ts.departmentCode from TechnicalStaff ts where ts.departmentCode is not null) " +
            "and ( upper(d.id) like  %:search% or upper(d.description) like %:search% " +
            "or upper(m.technicianName) like  %:search% or upper(m.id) like %:search% " +
            "or upper(c.technicianName) like  %:search% or upper(c.id)like %:search% " +
            "or upper(d.repairCenterDefault) like  %:search% or d.phone like  %:search% )")
    Page<Department> findForUsersExtraction(@Param("search") String search, Pageable paging);

    @Query("select d from Department d left join d.manager m left join d.contact c " +
            "where d.repairCenter = true  " +
            "and ( upper(d.id) like  %:search% or upper(d.description) like %:search% " +
            "or upper(m.technicianName) like  %:search% or upper(m.id) like %:search% " +
            "or upper(c.technicianName) like  %:search% or upper(c.id)like %:search% " +
            "or upper(d.repairCenterDefault) like  %:search% or d.phone like  %:search% )")
    Page<Department> findAllRepairCenter(@Param("search") String search, Pageable paging);
}
