package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {
    @Query("select a from Application a where upper(a.id) like %:search%" +
            " or upper(a.description) like %:search%")
    Page<Application> findLike(String search, Pageable paging);
}
