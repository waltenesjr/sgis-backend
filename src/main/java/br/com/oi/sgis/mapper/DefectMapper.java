package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.DefectDTO;
import br.com.oi.sgis.entity.Defect;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DefectMapper {

    DefectMapper INSTANCE = Mappers.getMapper(DefectMapper.class);

    DefectDTO toDTO(Defect defect);

    Defect toModel(DefectDTO dto);
}
