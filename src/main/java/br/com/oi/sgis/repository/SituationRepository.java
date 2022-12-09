package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Situation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SituationRepository extends JpaRepository<Situation, String> {

    @Query(value = "select s from Situation s where s.id in (:situationsCRP)")
    List<Situation> findCRPSituations(@Param("situationsCRP") List<String> situationsCRP);

    @Query(value = "select s from Situation s where upper(s.id) like %:search% " +
            " or upper(s.description) like %:search% ")
    Page<Situation> findLike(@Param("search") String search, Pageable paging);
}
