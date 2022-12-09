package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Technique;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TechniqueRepository extends JpaRepository<Technique, String> {

    @Query("select t from Technique t where upper(t.id) like %:search% or " +
            "upper(t.description) like %:search% or upper(t.techniqueType) like %:search% ")
    Page<Technique> findLike(@Param("search") String search, Pageable paging);
}
