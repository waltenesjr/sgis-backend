package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.AddressDTO;
import br.com.oi.sgis.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressMapper {

    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    @Mapping(target = "cgcCpf.addresses", ignore = true)
    Address toModel(AddressDTO addressDTO);

    @Mapping(target = "cgcCpf.addresses", ignore = true)
    AddressDTO toDTO(Address address);
}
