package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.CompanyDTO;
import br.com.oi.sgis.entity.Company;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {ManufacturerMapper.class, AddressMapper.class, CompanyMapper.class})
public interface CompanyMapper {

    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    Company toModel(CompanyDTO companyDTO);

    CompanyDTO toDTO(Company company);
}
