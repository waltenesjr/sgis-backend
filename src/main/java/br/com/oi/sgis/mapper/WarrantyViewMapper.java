package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.WarrantyViewDTO;
import br.com.oi.sgis.entity.view.WarrantyView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WarrantyViewMapper {

    WarrantyViewMapper INSTANCE = Mappers.getMapper(WarrantyViewMapper.class);

    WarrantyView toModel(WarrantyViewDTO warrantyViewDTO);
    WarrantyViewDTO toDTO(WarrantyView warrantyView);

}
