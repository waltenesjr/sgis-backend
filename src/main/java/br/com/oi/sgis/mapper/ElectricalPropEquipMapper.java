package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.ElectricalPropEquipDTO;
import br.com.oi.sgis.entity.ElectricalPropEquip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ElectricalPropEquipMapper {
    ElectricalPropEquipMapper INSTANCE = Mappers.getMapper(ElectricalPropEquipMapper.class);

    @Mapping(source = "property", target = "id.properties.id")
    @Mapping(source = "equipament", target = "id.equipament.id")
    ElectricalPropEquip toModel(ElectricalPropEquipDTO electricalPropEquipDTO);

    @Mapping(source = "id.properties.id", target = "property")
    @Mapping(source = "id.equipament.id", target = "equipament")
    ElectricalPropEquipDTO toDTO(ElectricalPropEquip electricalPropEquip);
}
