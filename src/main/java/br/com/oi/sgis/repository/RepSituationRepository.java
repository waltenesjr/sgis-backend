package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.RepSituation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepSituationRepository extends JpaRepository<RepSituation, String> {

    @Query(value = "SELECT rs  FROM RepSituation rs WHERE rs.id IN ( 'EGA', 'EGC', 'EGO', 'ERI', 'ERT', 'ECT', 'EOR', 'ACT')")
    List<RepSituation> listSituationsForwardRepair();

    @Query(value = "SELECT rs  FROM RepSituation rs WHERE rs.id IN (:repSituations)")
    List<RepSituation> listSelectedSituations(@Param("repSituations") List<String> repSituations);
}
