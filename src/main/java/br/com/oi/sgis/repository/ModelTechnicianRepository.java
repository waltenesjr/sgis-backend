package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.ModelTechnician;
import br.com.oi.sgis.entity.ModelTechnicianID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModelTechnicianRepository extends JpaRepository<ModelTechnician, ModelTechnicianID> {

    @Query("select mt from ModelTechnician mt where mt.id.department.id = :departmentId and" +
            " mt.id.model.id = :modelId and mt.id.technicalStaff.id = :techId")
    Optional<ModelTechnician> findById(@Param("modelId") String modelId,@Param("departmentId")  String departmentId, @Param("techId") String techId);

    @Query("select mt from ModelTechnician mt where mt.id.department.id = :departmentId and " +
            " (upper(mt.id.model.id) like %:search% or upper(mt.id.technicalStaff.id) like %:search% )")
    Page<ModelTechnician> findLike(@Param("search") String search, @Param("departmentId")  String departmentId, Pageable paging);
}
