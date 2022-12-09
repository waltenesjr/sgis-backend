package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.EstimateDTO;
import br.com.oi.sgis.entity.Estimate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {ContractMapper.class, CompanyMapper.class, TechnicalStaffMapper.class,
        DepartmentMapper.class, AddressMapper.class, ItemEstimateMapper.class})
public interface EstimateMapper {

    EstimateMapper INSTANCE = Mappers.getMapper(EstimateMapper.class);
    EstimateDTO toDTO(Estimate intervention);
    Estimate toModel(EstimateDTO interventionDTO);
}
