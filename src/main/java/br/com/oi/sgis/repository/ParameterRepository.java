package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ParameterRepository extends JpaRepository<Parameter, String> {

    Optional<Parameter> findTopByAbbreviationIsContaining(String abbreviation);

    @Query("select p from Parameter p where upper(p.id) like %:search% or " +
            " upper(p.abbreviation) like %:search% or upper(p.barcode) like %:search% or " +
            " upper(p.company.companyName) like %:search%  or upper(p.boxCode) like %:search%")
    Page<Parameter> findLike(@Param("search") String search, Pageable paging);

    @Modifying @Transactional
    @Query("update Parameter p set p.boxCode = :lastBoxCode where p.id = :id")
    void updateBoxCode(@Param("lastBoxCode") String lastBoxCode,@Param("id") String id);

    @Modifying @Transactional
    @Query("update Parameter p set p.barcode = :lastBarcode where p.id = :id")
    void updateBarcode(@Param("lastBarcode") String lastBarcode,@Param("id") String id);
}
