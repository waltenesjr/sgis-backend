package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponentRepository extends JpaRepository<Component, String> {
    @Query("select c from Component c where upper(c.id) like %:search% " +
            " or upper(c.description) like %:search% or upper(c.componentType.id)  like %:search% " +
            " or upper(c.componentType.description)  like %:search% ")
    Page<Component> findLike(String search, Pageable paging);
}
