package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.ComponentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponentTypeRepository extends JpaRepository<ComponentType, String> {
    @Query("select ct from ComponentType ct where upper(ct.id) like %:search%" +
            " or upper(ct.description) like %:search%")
    Page<ComponentType> findLike(@Param("search") String search, Pageable paging);
}
