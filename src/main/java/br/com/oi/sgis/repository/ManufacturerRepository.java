package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Manufacturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, String> {
    @Query("select m from Manufacturer m where upper(m.id) like %:search% " +
            " or upper(m.description) like %:search% ")
    Page<Manufacturer> findLike(String search, Pageable paging);
}
