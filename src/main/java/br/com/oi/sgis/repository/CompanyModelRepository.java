package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.CompanyModel;
import br.com.oi.sgis.entity.CompanyModelID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyModelRepository extends JpaRepository<CompanyModel, CompanyModelID> {

    @Query("select cm from CompanyModel cm where upper(cm.id.equipament.id) like %:search%  or " +
            "upper(cm.id.department.id) like %:search%  or upper(cm.id.company.id) like %:search% ")
    Page<CompanyModel> findLike(@Param("search") String search, Pageable paging);

    @Query("select cm from CompanyModel  cm where upper(cm.id.equipament.id) = :equipamentId and" +
            " upper(cm.id.company.id) = :companyId and upper(cm.id.department.id) = :departmentId ")
    Optional<CompanyModel> findById(@Param("equipamentId") String equipamentId, @Param("companyId") String companyId, @Param("departmentId") String departmentId);
}
