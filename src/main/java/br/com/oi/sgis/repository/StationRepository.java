package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface StationRepository extends JpaRepository<Station, String> {

    @Query("select s from Station s left join s.address a " +
            " left join s.uf u where upper(s.id) like %:search% " +
            " or upper(s.description) like %:search% or upper(u.id) like %:search% or " +
            " upper(a.addressDescription) like %:search% or s.number = :number")
    Page<Station> findLike(@Param("search") String search, @Param("number") BigDecimal number, Pageable paging);

}
