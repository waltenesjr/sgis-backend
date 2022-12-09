package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Diagnosis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, String> {
    @Query("select d from Diagnosis d where upper(d.id) like %:search%" +
            " or upper(d.description) like %:search%")
    Page<Diagnosis> findLike(@Param("search") String search, Pageable paging);
}
