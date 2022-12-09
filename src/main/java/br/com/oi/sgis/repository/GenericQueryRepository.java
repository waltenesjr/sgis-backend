package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.GenericQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GenericQueryRepository extends JpaRepository<GenericQuery, Long>, GenericQueryCustomRepository {

    @Query("select gc from GenericQuery gc left join gc.genericQueryType g left join gc.technicalStaff t " +
            "where upper(gc.title) like %:search% " +
            "or upper(g.id) like %:search% " +
            "or upper(t.id) like %:search%")
    Page<GenericQuery> findLike(Pageable paging, String search);

    GenericQuery findTopByOrderByIdDesc();
}
