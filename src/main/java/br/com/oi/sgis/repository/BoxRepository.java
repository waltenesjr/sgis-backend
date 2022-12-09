package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Box;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoxRepository extends JpaRepository<Box, String> {

    @Query("select b from Box b where upper(b.id) like %:search% ")
    Page<Box> findLike(@Param("search") String search, Pageable paging);


    @Modifying
    @Query("update Box b set b.station.id = :#{(#box.station != null ? #box.station.id : null)}, b.boxType = :#{#box.boxType}," +
            " b.description = :#{#box.description}, b.location = :#{#box.location}, b.active = :#{#box.active} " +
            " where b.id = :#{#box.id}")
    void updateBox(@Param("box") Box box);
}
