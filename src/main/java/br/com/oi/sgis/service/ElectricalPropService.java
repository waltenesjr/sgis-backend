package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.ElectricalProperty;
import br.com.oi.sgis.exception.EletricPropNotFoundException;
import br.com.oi.sgis.mapper.ElectricalPropMapper;
import br.com.oi.sgis.repository.ElectricalPropRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ElectricalPropService {

    private final ElectricalPropRepository electricalPropRepository;
    private static final ElectricalPropMapper electricalPropMapper = ElectricalPropMapper.INSTANCE;
    private final ReportService reportService;
    private final UnityService unityService;

    public List<ElectricalPropDTO> listAll() {
        List<ElectricalProperty> allElectricalProperties = electricalPropRepository.findAll();
        return allElectricalProperties.parallelStream().map(electricalPropMapper::toDTO).collect(Collectors.toList());
    }

    public ElectricalPropDTO findById(String id) throws EletricPropNotFoundException {
        ElectricalProperty electricalProperty = verifyIfExists(id);
        return electricalPropMapper.toDTO(electricalProperty);
    }

    private ElectricalProperty verifyIfExists(String id) throws EletricPropNotFoundException {
        return electricalPropRepository.findById(id)
                .orElseThrow(()-> new EletricPropNotFoundException(MessageUtils.ELETRIC_PROPERTY_NOT_FOUND_BY_ID.getDescription() + id) );
    }

    public PaginateResponseDTO<ElectricalPropDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(electricalPropRepository.findAll(paging).map(electricalPropMapper::toDTO));

        return PageableUtil.paginate(electricalPropRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(electricalPropMapper::toDTO));

    }
    public MessageResponseDTO createElectricalProperty(ElectricalPropDTO electricalPropDTO) {
        Optional<ElectricalProperty> existElectricalProperty = electricalPropRepository.findById(electricalPropDTO.getId());
        if(existElectricalProperty.isPresent())
            throw new IllegalArgumentException(MessageUtils.ALREADY_EXISTS.getDescription());
        try {
            ElectricalProperty electricalProp = electricalPropMapper.toModel(electricalPropDTO);
            electricalPropRepository.save(electricalProp);
            return createMessageResponse(electricalProp.getId(), MessageUtils.ELETRIC_PROPERTY_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ELETRIC_PROPERTY_SAVE_ERROR.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    public MessageResponseDTO updateElectricalProperty(ElectricalPropDTO electricalPropDTO) throws EletricPropNotFoundException {
        verifyIfExists(electricalPropDTO.getId());
        try {
            ElectricalProperty electricalProp = electricalPropMapper.toModel(electricalPropDTO);
            electricalPropRepository.save(electricalProp);
            return createMessageResponse(electricalProp.getId(), MessageUtils.ELETRIC_PROPERTY_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ELETRIC_PROPERTY_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] electricalPropReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<ElectricalPropDTO> electricalPropDTOS =  listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(electricalPropDTOS== null || electricalPropDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Propriedades Elétricas / Físicas");
        parameters.put("column1", "CODIGO");
        parameters.put("column2", "DESCRIÇÃO");

        List<GenericReportDTO> genericReport = electricalPropDTOS.stream().map(d ->
                GenericReportDTO.builder().data1(d.getId()).data2(d.getDescription()).build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);

    }

    public void deleteById(String id) throws EletricPropNotFoundException {
        verifyIfExists(id);
        try{
            electricalPropRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ELETRIC_PROPERTY_DELETE_ERROR.getDescription());
        }
    }

    public PaginateResponseDTO<PhysicalElectricalPropsDTO> physicalElectricalProperty(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, ElectricalPropFilterDTO filterDto) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        return PageableUtil.paginate(electricalPropRepository.findPhysicalElectricalProperties(filterDto, paging));

    }

    public byte[] physicalElectricalPropertyReport(List<String> sortAsc, List<String> sortDesc, ElectricalPropFilterDTO filterDto) throws JRException, IOException {
        List<PhysicalElectricalPropsDTO> electricalPropDTOS =  physicalElectricalProperty(0, Integer.MAX_VALUE, sortAsc, sortDesc, filterDto).getData();
        if(electricalPropDTOS== null || electricalPropDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        List<PhysicalElectricalPropsReportDTO> reportData = new ArrayList<>();
        Map<String, List<PhysicalElectricalPropsDTO>> propGroupByUnity = electricalPropDTOS.parallelStream().collect(groupingBy(PhysicalElectricalPropsDTO::getBarcode, LinkedHashMap::new,
                Collectors.toList()));
        propGroupByUnity.forEach((key, value)->{
            UnityDTO unityDTO = unityService.findById(key);
            BigDecimal totalLenght = value.stream().filter( v -> v.getId().equals("COMP")).map(PhysicalElectricalPropsDTO::getMeasureUnity).reduce( BigDecimal::add).orElse(BigDecimal.ZERO);
            PhysicalElectricalPropsReportDTO data = PhysicalElectricalPropsReportDTO.builder().barcode(key)
                    .station(unityDTO.getStation() !=null ? unityDTO.getStation().getId() : "")
                    .deposit(unityDTO.getDeposit().getId()).responsible(unityDTO.getResponsible().getId())
                    .unityCode(unityDTO.getUnityCode().getId()).items(value).totalLength(totalLenght.setScale(2, RoundingMode.HALF_UP)).build();
            reportData.add(data);
        });
        Map<String, Object> parameters = new HashMap<>();
        return reportService.physicalElectricalPropertyReport(reportData, parameters);
    }


}
