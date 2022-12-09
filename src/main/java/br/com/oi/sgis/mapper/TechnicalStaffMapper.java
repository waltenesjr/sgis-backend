package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.TechnicalStaffDTO;
import br.com.oi.sgis.entity.TechnicalStaff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {DepartmentMapper.class, ParameterMapper.class})
public interface TechnicalStaffMapper {
    TechnicalStaffMapper INSTANCE = Mappers.getMapper(TechnicalStaffMapper.class);

    TechnicalStaff toModel(TechnicalStaffDTO technicalStaffDTO);

    @Mapping(target = "levels", source = "technicalStaff.userRegister.levels")
    @Mapping(target = "companyName", source = "technicalStaff.cgcCpfCompany.company.companyName")
    TechnicalStaffDTO toDTO(TechnicalStaff technicalStaff);
}
