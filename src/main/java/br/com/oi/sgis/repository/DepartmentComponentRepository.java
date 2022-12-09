package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.DepartmentComponent;
import br.com.oi.sgis.entity.DepartmentComponentID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartmentComponentRepository extends JpaRepository<DepartmentComponent, DepartmentComponentID> {

    @Query("select dc from DepartmentComponent dc where dc.id.department.id = :departmentId")
    Page<DepartmentComponent> findByDepartment(@Param("departmentId") String departmentId, Pageable paging);
}
