package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.ModelEquipamentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ModelEquipTypeRepository extends JpaRepository<ModelEquipamentType, String> {

    @Query("select me from ModelEquipamentType me left join me.company c " +
            " left join me.technician t where upper(me.id) like %:search% " +
            "or upper(me.mnemonic) like %:search% or upper(me.description) like %:search% " +
            "or upper(c.id) like %:search% or upper(c.cnpjCpf) like %:search% " +
            "or upper(t.id) like %:search% or upper(t.technicianName) like %:search%")
    Page<ModelEquipamentType> findLike(@Param("search") String search, Pageable pageable);

    @Modifying @Transactional
    @Query(value = "UPDATE EQUIPAMENTO_AREA EA SET EA.FLAG_DESCONT = '0', EA.TECNICO = :technicianId" +
            " WHERE EA.COD_MODELO_EQTO = :meId ", nativeQuery = true)
    void updateEquipByModelDesct(@Param("meId")String meId, @Param("technicianId") String technicianId);
}
