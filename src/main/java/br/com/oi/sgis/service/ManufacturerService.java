package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.GenericReportDTO;
import br.com.oi.sgis.dto.ManufacturerDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Manufacturer;
import br.com.oi.sgis.exception.ManufacturerNotFoundException;
import br.com.oi.sgis.mapper.ManufacturerMapper;
import br.com.oi.sgis.repository.ManufacturerRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;
    private static final ManufacturerMapper manufacturerMapper = ManufacturerMapper.INSTANCE;
    private final ReportService reportService;

    public PaginateResponseDTO<ManufacturerDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if(term.isBlank())
            return PageableUtil.paginate(manufacturerRepository.findAll(paging).map(manufacturerMapper::toDTO));

        return PageableUtil.paginate( manufacturerRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(manufacturerMapper::toDTO));
    }

    public ManufacturerDTO findById(String id) throws ManufacturerNotFoundException {
        Manufacturer manufacturer = verifyIfExists(id);
        return manufacturerMapper.toDTO(manufacturer);
    }

    private Manufacturer verifyIfExists(String id) throws ManufacturerNotFoundException {
        return manufacturerRepository.findById(id)
                .orElseThrow(()-> new ManufacturerNotFoundException(MessageUtils.MANUFACTURER_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public MessageResponseDTO createManufacturer(ManufacturerDTO manufacturerDTO) {
        Optional<Manufacturer> existManufacturer = manufacturerRepository.findById(manufacturerDTO.getId());
        if(existManufacturer.isPresent())
            throw new IllegalArgumentException(MessageUtils.ALREADY_EXISTS.getDescription());
        try {
            Manufacturer manufacturer = manufacturerMapper.toModel(manufacturerDTO);
            manufacturerRepository.save(manufacturer);
            return createMessageResponse(manufacturer.getId(), MessageUtils.MANUFACTURER_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.MANUFACTURER_SAVE_ERROR.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    public MessageResponseDTO updateManufacturer(ManufacturerDTO manufacturerDTO) throws ManufacturerNotFoundException {
        verifyIfExists(manufacturerDTO.getId());
        try {
            Manufacturer manufacturer = manufacturerMapper.toModel(manufacturerDTO);
            manufacturerRepository.save(manufacturer);
            return createMessageResponse(manufacturer.getId(), MessageUtils.MANUFACTURER_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.MANUFACTURER_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] manufacturerReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<ManufacturerDTO> manufacturerDTOS = listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(manufacturerDTOS== null || manufacturerDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Fabricantes");
        parameters.put("column1", "CÓDIGO");
        parameters.put("column2", "DESCRIÇÃO");

        List<GenericReportDTO> genericReport = manufacturerDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(r.getId()).data2(r.getDescription()).build()
        ).collect(Collectors.toList());

        return reportService.genericReport(genericReport, parameters);

    }

    public void deleteById(String id) throws ManufacturerNotFoundException {
        verifyIfExists(id);
        try{
            manufacturerRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.MANUFACTURER_DELETE_ERROR.getDescription());
        }
    }

}
