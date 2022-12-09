package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.ElectricalPropFilterDTO;
import br.com.oi.sgis.dto.PhysicalElectricalPropsDTO;
import br.com.oi.sgis.entity.ElectricalProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectricalPropRepository extends JpaRepository<ElectricalProperty, String> {
    @Query("select ep from ElectricalProperty ep where upper(ep.id) like %:search% or " +
            "upper(ep.description) like %:search% or upper(ep.measurement.id) like %:search% ")
    Page<ElectricalProperty> findLike(@Param("search") String search, Pageable paging);

    @Query("select ep from ElectricalPropUnity eu inner join eu.id.properties ep where " +
            "eu.id.unity.id = :barcode")
    List<ElectricalProperty> findElectricalPropertiesByUnity(@Param("barcode") String barcode);

    @Query("select new br.com.oi.sgis.dto.PhysicalElectricalPropsDTO(d.id, d.description, b.measure, b.value, d.measurement.id, b.value , b.id.unity.id, a.unityCode.id, concat(a.responsible.id, ' - ', a.responsible.description), " +
            "concat(a.unityCode.equipModelCode.id, ' - ', a.unityCode.equipModelCode.description), concat(a.unityCode.equipModelCode.equipamentType.id, ' - ', a.unityCode.equipModelCode.equipamentType.equipamentName)) from   ElectricalPropUnity b " +
            " inner join b.id.unity a inner join b.id.properties d  where (a.id like %:#{#dto.barcode}%  or (:#{#dto.barcode} is null or :#{#dto.barcode}  = '' ))" +
            " and (a.unityCode.id like %:#{#dto.modelUnity}%  or (:#{#dto.modelUnity} is null or :#{#dto.modelUnity}  = '' )) " +
            " and (a.responsible.id like %:#{#dto.responsible}%  or (:#{#dto.responsible} is null or :#{#dto.responsible}  = '' )) " +
            " and (a.unityCode.equipModelCode.equipamentType.id like %:#{#dto.equipmentType}%  or (:#{#dto.equipmentType} is null or :#{#dto.equipmentType}  = '' )) " +
            " and (a.unityCode.equipModelCode.id like %:#{#dto.equipmentModel}%  or (:#{#dto.equipmentModel} is null or :#{#dto.equipmentModel}  = '' ))" )
    Page<PhysicalElectricalPropsDTO> findPhysicalElectricalProperties(@Param("dto")ElectricalPropFilterDTO dto, Pageable paging);

}
