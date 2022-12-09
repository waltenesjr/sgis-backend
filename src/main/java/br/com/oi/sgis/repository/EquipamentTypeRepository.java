package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.EquipamentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface EquipamentTypeRepository extends JpaRepository<EquipamentType, String> {

    @Query("select et from EquipamentType et left join et.application a left join et.technique t " +
            "where upper(et.id) like  %:search% or upper(et.equipamentName) like  %:search%  " +
            "or a.id like  %:search%  or a.description like  %:search% " +
            "or t.id like  %:search%  or t.description like  %:search% ")
    Page<EquipamentType> findLike(String search, Pageable paging);
}
