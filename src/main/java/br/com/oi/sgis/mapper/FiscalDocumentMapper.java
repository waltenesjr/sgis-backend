package br.com.oi.sgis.mapper;

import br.com.oi.sgis.dto.FiscalDocumentDTO;
import br.com.oi.sgis.entity.FiscalDocument;
import br.com.oi.sgis.entity.FiscalDocumentId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper(uses = {CompanyMapper.class, ContractMapper.class, FiscalDocumentId.class})
public interface FiscalDocumentMapper {
    FiscalDocumentMapper INSTANCE = Mappers.getMapper(FiscalDocumentMapper.class);


    @Mapping(source = "fiscalDocumentDTO.docNumber", target = "id.docNumber")
    @Mapping(source = "fiscalDocumentDTO.docDate", target = "id.docDate")
    @Mapping(source = "fiscalDocumentDTO.companyId", target = "id.cgcCPf.id")
    @Mapping(source = "fiscalDocumentDTO.companyName", target = "id.cgcCPf.companyName")
    @Mapping(source = "fiscalDocumentDTO.currencyType.id", target = "currencyType")
    FiscalDocument toModel(FiscalDocumentDTO fiscalDocumentDTO);

    @Mapping(target = "docNumber", source = "fiscalDocument.id.docNumber")
    @Mapping(target = "docDate", source = "fiscalDocument.id.docDate")
    @Mapping(target = "companyId", source = "fiscalDocument.id.cgcCPf.id")
    @Mapping(target = "companyName", source = "fiscalDocument.id.cgcCPf.companyName")
    @Mapping(source = "currencyType", target = "currencyType")
    FiscalDocumentDTO toDTO(FiscalDocument fiscalDocument);

    static Map<String, String> getMappedValues(){
        Map<String, String> mappedValues = new HashMap<>();
        mappedValues.put("docNumber", "id.docNumber");
        mappedValues.put("docDate", "id.docDate");
        mappedValues.put("companyId", "id.cgcCPf.id");
        mappedValues.put("companyName", "id.cgcCPf.companyName");
        return mappedValues;
    }
}
