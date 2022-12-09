package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.ModelContracDTO;
import br.com.oi.sgis.entity.ModelContract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ModelContractMapper {

    ModelContractMapper INSTANCE = Mappers.getMapper(ModelContractMapper.class);

    @Mapping(source = "contract", target = "id.contract.id")
    @Mapping(source = "model", target = "id.model.id")
    ModelContract toModel(ModelContracDTO dto);

    @Mapping(source = "id.contract.id", target = "contract")
    @Mapping(source = "id.model.id", target = "model")
    ModelContracDTO toDto(ModelContract modelContract);
}
