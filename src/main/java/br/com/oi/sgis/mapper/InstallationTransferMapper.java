package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.InstallationTransferDTO;
import br.com.oi.sgis.entity.Unity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {CompanyMapper.class, AddressMapper.class})
public interface InstallationTransferMapper {

    InstallationTransferMapper INSTANCE = Mappers.getMapper(InstallationTransferMapper.class);

    InstallationTransferDTO toDTO(Unity unity);

}
