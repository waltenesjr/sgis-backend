package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.BoxType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoxTypeRepository extends JpaRepository<BoxType, String> {
    @Query("select bt from BoxType bt where upper(bt.id) like %:search%" +
            " or upper(bt.description) like %:search%")
    Page<BoxType> findLike(@Param("search") String search, Pageable paging);
}
