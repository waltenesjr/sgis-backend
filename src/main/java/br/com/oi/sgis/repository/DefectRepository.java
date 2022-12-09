package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Defect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DefectRepository extends JpaRepository<Defect, String> {
    @Query("select d from Defect d where upper(d.id) like %:search%" +
            " or upper(d.description) like %:search%")
    Page<Defect> findLike(String search, Pageable paging);
}
