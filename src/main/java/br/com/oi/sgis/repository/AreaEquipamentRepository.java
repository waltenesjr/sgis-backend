package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.AreaEquipament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;


@Repository
public interface AreaEquipamentRepository extends JpaRepository<AreaEquipament, String> {


    Page<AreaEquipament> findAllByDiscontinuedFlagFalse(Pageable pageable);

    @Query("select ae from AreaEquipament ae left join ae.equipModelCode me " +
            " left join ae.technician t left join ae.company c left join me.equipamentType et " +
            " where upper(ae.id) like %:search% or upper(ae.mnemonic) like %:search% or " +
            " upper(ae.description) like %:search% or upper(me.id) like %:search%  or " +
            " upper(et.id) like %:search% or upper(c.id) like %:search% or upper(ae.fiscalClassification) like %:search%   " +
            " or upper(t.id) like %:search% or ae.value = :number or ae.weight = :number  " +
            " and ae.discontinuedFlag = false ")
    Page<AreaEquipament> findLike(@Param("search") String search, @Param("number") BigDecimal number, Pageable pageable);

    @Query("select ae from AreaEquipament ae left join ae.equipModelCode me " +
            " left join ae.technician t left join ae.company c left join me.equipamentType et " +
            " where (upper(ae.id) like %:search% or upper(ae.mnemonic) like %:search% or " +
            " upper(ae.description) like %:search% or upper(me.id) like %:search%  or " +
            " upper(et.id) like %:search% or upper(c.id) like %:search% or upper(ae.fiscalClassification) like %:search%   " +
            " or upper(t.id) like %:search% or ae.value = :number or ae.weight = :number)  " +
            " and ae.date = :date  and ae.discontinuedFlag = false ")
    Page<AreaEquipament> findLikeWithDate(@Param("search") String search, @Param("number") BigDecimal number, @Param("date") LocalDateTime date, Pageable pageable);


    @Query("select distinct ae.mnemonic from AreaEquipament ae where ae.mnemonic is not null")
    Page<Object> findAllByDistinctMnemonic(Pageable paging);

    @Query("select distinct ae.mnemonic from AreaEquipament ae where  upper(ae.mnemonic) like %:search%")
    Page<Object> findAllByDistinctMnemonic(@Param("search") String search,Pageable paging);

    Optional<AreaEquipament> findTopByMnemonicContains(String mnemonic);

}
