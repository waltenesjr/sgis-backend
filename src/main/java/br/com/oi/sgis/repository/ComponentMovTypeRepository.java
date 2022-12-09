package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.ComponentMovType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponentMovTypeRepository extends JpaRepository<ComponentMovType, String> {
    @Query("select ct from ComponentMovType ct where upper(ct.id) like %:search%" +
            " or upper(ct.description) like %:search%")
    Page<ComponentMovType> findLike(@Param("search") String search, Pageable paging);
}
