package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Measurement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeasurementRepository extends JpaRepository<Measurement, String> {

    @Query("select m from Measurement m where upper(m.id) like %:search% or " +
            "upper(m.description) like %:search% ")
    Page<Measurement> findLike(@Param("search") String search, Pageable paging);
}
