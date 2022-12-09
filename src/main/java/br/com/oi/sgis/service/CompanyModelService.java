package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.CompanyModelDTO;
import br.com.oi.sgis.dto.GenericReportDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.CompanyModel;
import br.com.oi.sgis.entity.CompanyModelID;
import br.com.oi.sgis.exception.CompanyModelNotFoundException;
import br.com.oi.sgis.mapper.CompanyModelMapper;
import br.com.oi.sgis.repository.CompanyModelRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import br.com.oi.sgis.util.Utils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CompanyModelService {

    private final CompanyModelRepository companyModelRepository;
    private static final CompanyModelMapper companyModelMapper = CompanyModelMapper.INSTANCE;
    private final ReportService reportService;
    private final CompanyService companyService;
    private final AreaEquipamentService areaEquipamentService;

    public PaginateResponseDTO<CompanyModelDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap = CompanyModelMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, sortMap));
        if (term.isBlank())
            return PageableUtil.paginate(companyModelRepository.findAll(paging).map(companyModelMapper::toDTO), sortMap);

        return PageableUtil.paginate(companyModelRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(companyModelMapper::toDTO), sortMap);

    }

    public CompanyModelDTO findByIdDTO(CompanyModelDTO dto) throws CompanyModelNotFoundException {
        CompanyModelID id = companyModelMapper.toModel(dto).getId();
        CompanyModel companyModel = verifyIfExists(id);
        return companyModelMapper.toDTO(companyModel);
    }

    private CompanyModel verifyIfExists(CompanyModelID id) throws CompanyModelNotFoundException {
        return companyModelRepository.findById(id.getEquipament().getId(),id.getCompany().getId(), id.getDepartment().getId())
                .orElseThrow(()-> new CompanyModelNotFoundException(MessageUtils.COMPANY_MODEL_NOT_FOUND_BY_ID.getDescription()));
    }

  public MessageResponseDTO createCompanyModel(CompanyModelDTO companyModelDTO) {
        companyModelDTO.setDepartment(Utils.getUser().getDepartmentCode());
        CompanyModel companyModel = companyModelMapper.toModel(companyModelDTO);
        Optional<CompanyModel> existCompanyModel = companyModelRepository.findById(companyModel.getId());
        if (existCompanyModel.isPresent())
            throw new IllegalArgumentException(MessageUtils.COMPANY_MODEL_ALREADY_EXISTS.getDescription());
        validate(companyModelDTO);
        try {
            if(companyModel.getPriority() == null)
                companyModel.setPriority(0L);
            companyModelRepository.save(companyModel);
            return createMessageResponse(MessageUtils.COMPANY_MODEL_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.COMPANY_MODEL_SAVE_ERROR.getDescription());
        }
    }

    @SneakyThrows
    private void validate(CompanyModelDTO companyModelDTO) {
        companyService.findById(companyModelDTO.getCompany().getId());
        areaEquipamentService.findById(companyModelDTO.getEquipament().getId());
    }

    private MessageResponseDTO createMessageResponse(String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message).status(status).build();
    }

    public MessageResponseDTO updateCompanyModel(CompanyModelDTO companyModelDTO) throws CompanyModelNotFoundException {
        validate(companyModelDTO);
        CompanyModel companyModel = companyModelMapper.toModel(companyModelDTO);
        verifyIfExists(companyModel.getId());
        try {
            companyModelRepository.save(companyModel);
            return createMessageResponse(MessageUtils.COMPANY_MODEL_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.COMPANY_MODEL_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] companyModelReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<CompanyModelDTO> companyModelDTOS = listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(companyModelDTOS== null || companyModelDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Modelos por Empresa Mantenedora");
        parameters.put("column1", "EMPRESA");
        parameters.put("column2", "MODELO DE UNIDADE");
        parameters.put("column3", "DESCRIÇÃO");

        List<GenericReportDTO> genericReport = companyModelDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(r.getCompany().getId()).data2(r.getEquipament().getId()).data3(r.getEquipament().getDescription()).build()
        ).collect(Collectors.toList());

        return reportService.genericReportThreeColumns(genericReport, parameters);

    }

    public void deleteById(CompanyModelDTO dto) throws CompanyModelNotFoundException {
        validate(dto);
        CompanyModel companyModel = companyModelMapper.toModel(dto);
        verifyIfExists(companyModel.getId());
        try{
            companyModelRepository.deleteById(companyModel.getId());
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.COMPANY_MODEL_DELETE_ERROR.getDescription());
        }
    }

}
