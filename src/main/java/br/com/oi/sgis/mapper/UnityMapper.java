package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.UnityDTO;
import br.com.oi.sgis.entity.Unity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {FiscalDocumentMapper.class, ElectricalPropUnityMapper.class, BoxMapper.class, AddressMapper.class})
public interface UnityMapper {

    UnityMapper INSTANCE = Mappers.getMapper(UnityMapper.class);

    @Mapping(target = "sapStatus", ignore = true)
    @Mapping(target = "descriptionProcedure", ignore = true)
    Unity toModel(UnityDTO unityDTO);

    @Mapping(target = "sapStatusDescription", source = "sapStatus")
    UnityDTO toDTO(Unity unity);
}
