package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.AreaEquipamentDTO;
import br.com.oi.sgis.entity.AreaEquipament;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {CompanyMapper.class, ModelEquipTypeMapper.class,TechnicalStaffMapper.class, ElectricalPropEquipMapper.class})
public interface AreaEquipamentMapper {

    AreaEquipamentMapper INSTANCE = Mappers.getMapper(AreaEquipamentMapper.class);

    AreaEquipament toModel(AreaEquipamentDTO areaEquipamentDTO);

    AreaEquipamentDTO toDTO(AreaEquipament areaEquipament);

}
