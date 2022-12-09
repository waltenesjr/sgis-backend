package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Intervention;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InterventionRepository extends JpaRepository<Intervention, String> {
    @Query("select i from Intervention i where upper(i.id) like %:search% or " +
            " upper(i.description) like %:search% ")
    Page<Intervention> findLike(@Param("search") String search, Pageable paging);
}
