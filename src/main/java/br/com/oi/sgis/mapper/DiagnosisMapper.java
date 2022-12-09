package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.DiagnosisDTO;
import br.com.oi.sgis.entity.Diagnosis;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DiagnosisMapper {
    DiagnosisMapper INSTANCE = Mappers.getMapper(DiagnosisMapper.class);
    Diagnosis toModel(DiagnosisDTO dto);
    DiagnosisDTO toDTO(Diagnosis diagnosis);
}
