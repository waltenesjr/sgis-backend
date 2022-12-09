package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.CableMovement;
import br.com.oi.sgis.exception.CableMovementNotFoundException;
import br.com.oi.sgis.mapper.CableMovementMapper;
import br.com.oi.sgis.mapper.ElectricalPropMapper;
import br.com.oi.sgis.repository.CableMovementRepository;
import br.com.oi.sgis.repository.ElectricalPropRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import br.com.oi.sgis.util.Utils;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CableMovementService {

    private final CableMovementRepository cableMovementRepository;
    private static final CableMovementMapper cableMovementMapper = CableMovementMapper.INSTANCE;
    private final ReportService reportService;
    private final ElectricalPropRepository electricalPropRepository;

    public PaginateResponseDTO<CableMovementDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, CableMovementFilterDTO filterDTO) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap = CableMovementMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, sortMap));

        return PageableUtil.paginate(cableMovementRepository.findLikeFilter(filterDTO, paging).map(cableMovementMapper::toDTO), sortMap);

    }

    public CableMovementDTO findById(Long sequence, String unityId) throws CableMovementNotFoundException {
        CableMovement cableMovement = verifyIfExists(sequence, unityId);
        return cableMovementMapper.toDTO(cableMovement);
    }

    private CableMovement verifyIfExists(Long sequence, String id) throws CableMovementNotFoundException {
        return cableMovementRepository.findById(sequence,id)
                .orElseThrow(()-> new CableMovementNotFoundException(MessageUtils.CABLE_MOV_NOT_FOUND_BY_ID.getDescription()));
    }

    private Long verifyNewCableMovementId() {
        Long lastId = cableMovementRepository.findLastId();
        return lastId++;
    }

    public MessageResponseDTO createCableMovement(CableMovementDTO cableMovementDTO) {
        try {
            cableMovementDTO.setTechnician(Utils.getUser());
            CableMovement cableMovement = cableMovementMapper.toModel(cableMovementDTO);
            Long id = verifyNewCableMovementId();
            cableMovement.getId().setSequence(id);
            cableMovementRepository.save(cableMovement);
            return createMessageResponse(MessageUtils.CABLE_MOV_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.CABLE_MOV_SAVE_ERROR.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message).status(status).build();
    }

    public byte[] cableMovementReport(CableMovementFilterDTO filterDTO, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<CableMovementDTO> cableMovementDTOS = listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, filterDTO).getData();
        if(cableMovementDTOS== null || cableMovementDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        return reportService.fillCableMovReport(cableMovementDTOS, parameters);

    }

    public List<ElectricalPropDTO> listCableMovementUnityProperties(String barcode){
        ElectricalPropMapper mapper = ElectricalPropMapper.INSTANCE;
        List<ElectricalPropDTO> electricalPropDTOS = electricalPropRepository.findElectricalPropertiesByUnity(barcode).stream()
                .map(mapper::toDTO).collect(Collectors.toList());
        if(electricalPropDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.CABLE_MOV_PROP_ERROR.getDescription());
        return electricalPropDTOS;
    }

    public PaginateResponseDTO<CableMovementDTO> getCableMovement(CableMovementQueryDTO cableMovementQueryDTO, Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap = CableMovementMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, sortMap));
        if(cableMovementQueryDTO.getInitialDate() != null ) {
            Utils.isPeriodInvalid(cableMovementQueryDTO.getInitialDate(), cableMovementQueryDTO.getFinalDate());
            return PageableUtil.paginate(cableMovementRepository.findCableMovementWithDateFilter(cableMovementQueryDTO, paging).map(cableMovementMapper::toDTO), sortMap);
        }
        return PageableUtil.paginate(cableMovementRepository.findCableMovement(cableMovementQueryDTO, paging).map(cableMovementMapper::toDTO), sortMap);
    }

    public byte[] cableMovementQueryReport(CableMovementQueryDTO cableMovementQueryDTO, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<CableMovementDTO> cableMovementDTOS = getCableMovement(cableMovementQueryDTO, 0, Integer.MAX_VALUE, sortAsc, sortDesc).getData();
        if(cableMovementDTOS== null || cableMovementDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("barcode", cableMovementQueryDTO.getBarcode());
        return reportService.fillCableMovQueryReport(cableMovementDTOS, parameters);
    }

}
