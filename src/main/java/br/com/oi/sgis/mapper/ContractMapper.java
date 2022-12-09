package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.ContractDTO;
import br.com.oi.sgis.entity.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {CompanyMapper.class, DepartmentMapper.class, ModelContractMapper.class, AddressMapper.class})
public interface ContractMapper {
    ContractMapper INSTANCE = Mappers.getMapper(ContractMapper.class);

    Contract toModel(ContractDTO dto);
    ContractDTO toDTO(Contract contract);
}
