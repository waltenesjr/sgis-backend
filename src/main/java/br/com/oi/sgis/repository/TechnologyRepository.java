package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Technology;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface TechnologyRepository extends JpaRepository<Technology, String> {
    @Query("select t from Technology t where upper(t.id) like %:search%" +
            " or upper(t.description) like %:search%")
    Page<Technology> findLike(@Param("search") String search, Pageable paging);
}
