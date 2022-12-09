package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.DepartmentUnity;
import br.com.oi.sgis.entity.DepartmentUnityID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface DepartmentUnityRepository extends JpaRepository<DepartmentUnity, DepartmentUnityID>, StockSummaryRepository {

    @Query("select du from DepartmentUnity du left join du.station s where upper(du.id.department.id) like %:search%  or " +
            "upper(du.id.equipament.id) like %:search%  or  upper(s.id) like %:search%  or " +
            "upper(du.location) like %:search% ")
    Page<DepartmentUnity> findLike(@Param("search") String search, Pageable paging);

    @Override
    @Query("select du from DepartmentUnity du where du.id.equipament.id = :#{#du.equipament.id} and " +
            " du.id.department.id = :#{#du.department.id}")
    Optional<DepartmentUnity> findById(@Param("du") DepartmentUnityID du);

    @Modifying @Transactional
    @Query("update Unity u set u.station = :#{#du.station}, u.location = :#{#du.location} " +
            "where u.responsible = :#{#du.id.department} and u.unityCode = :#{#du.id.equipament} " +
            " and u.situationCode.id = 'DIS' ")
    void updateUnitiesByDepartmentUnity(@Param("du") DepartmentUnity du);
}
