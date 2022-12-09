package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Central;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CentralRepository extends JpaRepository<Central, String> {

    @Query("select c from Central c left join c.station s where upper(c.id) like %:search% " +
            " or upper(c.description) like %:search% " +
            " or upper(c.prefix) like %:search%  or upper(c.tipping) like %:search%  " +
            " or upper(s.id) like %:search% or upper(c.description) like %:search% ")
    Page<Central> findLike(String search, Pageable paging);
}
