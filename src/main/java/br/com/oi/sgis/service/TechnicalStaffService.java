package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.Level;
import br.com.oi.sgis.entity.TechnicalStaff;
import br.com.oi.sgis.exception.TechnicalStaffNotFoundException;
import br.com.oi.sgis.mapper.TechnicalStaffMapper;
import br.com.oi.sgis.repository.DepartmentRepository;
import br.com.oi.sgis.repository.TechnicalStaffRepository;
import br.com.oi.sgis.repository.UnityRepository;
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
public class TechnicalStaffService {

    private TechnicalStaffRepository technicalStaffRepository;
    private static final TechnicalStaffMapper technicalStaffMapper = TechnicalStaffMapper.INSTANCE;
    private final ReportService reportService;
    private final NasphService nasphService;
    private final UnityRepository unityRepository;
    private final DepartmentRepository departmentRepository;

    @SneakyThrows
    public TechnicalStaffDTO findById(String id) throws TechnicalStaffNotFoundException{
        TechnicalStaff technicalStaff = verifyIfExists(id);
        return technicalStaffMapper.toDTO(technicalStaff);
    }

    private TechnicalStaff verifyIfExists(String id) throws TechnicalStaffNotFoundException {
        return technicalStaffRepository.findById(id)
                .orElseThrow(() -> new TechnicalStaffNotFoundException(MessageUtils.TECHNICAL_STAFF_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public PaginateResponseDTO<TechnicalStaffDTO> listPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term){
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if(term.isBlank())
            return PageableUtil.paginate(technicalStaffRepository.findAll(paging).map(technicalStaffMapper::toDTO));
        return  PageableUtil.paginate( technicalStaffRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(technicalStaffMapper::toDTO));
    }

    public MessageResponseDTO createTechnicalStaff(TechnicalStaffDTO technicalStaffDTO) {
        Optional<TechnicalStaff> existTechnicalStaff = technicalStaffRepository.findById(technicalStaffDTO.getId());
        if(existTechnicalStaff.isPresent())
            throw new IllegalArgumentException(MessageUtils.TECHNICAL_STAFF_ALREADY_EXISTS.getDescription());

        validateActive(technicalStaffDTO);
        try {
            TechnicalStaff technicalStaff = technicalStaffMapper.toModel(technicalStaffDTO);
            technicalStaffRepository.save(technicalStaff);
            return createMessageResponse(technicalStaff.getId(), MessageUtils.TECHNICAL_STAFF_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.TECHNICAL_STAFF_SAVE_ERROR.getDescription());
        }
    }

    private void validateActive(TechnicalStaffDTO technicalStaffDTO) {
        List<Integer> userLevels = Utils.getUser().getLevels().stream().map(Level::getLvl).collect(Collectors.toList());
        boolean isAdmin = userLevels.contains(0);
        if(!technicalStaffDTO.isActive() && !isAdmin)
            throw new IllegalArgumentException(MessageUtils.TECHNICAL_STAFF_SAVE_ERROR_ADMIN.getDescription());
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().title("Sucesso!").message(message + id).status(status).build();
    }

    public MessageResponseDTO updateTechnicalStaff(TechnicalStaffDTO technicalStaffDTO) throws TechnicalStaffNotFoundException {
        verifyIfExists(technicalStaffDTO.getId());
        try {
            TechnicalStaff technicalStaff = technicalStaffMapper.toModel(technicalStaffDTO);
            technicalStaffRepository.save(technicalStaff);
            return createMessageResponse(technicalStaff.getId(), MessageUtils.TECHNICAL_STAFF_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.TECHNICAL_STAFF_UPDATE_ERROR.getDescription());
        }
    }

    public MessageResponseDTO updateManHour(TechnicalStaffDTO technicalStaffDTO) throws TechnicalStaffNotFoundException {
        verifyIfExists(technicalStaffDTO.getId());
        try {
            TechnicalStaff technicalStaff = technicalStaffMapper.toModel(technicalStaffDTO);
            technicalStaffRepository.updateManHour(technicalStaff.getId(), technicalStaff.getManHourValue());
            return createMessageResponse(technicalStaff.getId(), MessageUtils.TECHNICAL_STAFF_UPDATE_MH_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.TECHNICAL_STAFF_UPDATE_MH_ERROR.getDescription());
        }
    }

    public byte[] technicalStaffReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<TechnicalStaffDTO> technicalStaffDTOS = listPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(technicalStaffDTOS== null || technicalStaffDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Pessoal Técnico");
        parameters.put("column1", "MATRÍCULA");
        parameters.put("column2", "NOME DO TÉCNICO");
        parameters.put("column3", "LOTAÇÃO");

        List<GenericReportDTO> genericReport = technicalStaffDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(r.getId()).data2(r.getTechnicianName())
                        .data3(r.getDepartmentCode()==null ? "" : r.getDepartmentCode().getId()).build()
        ).collect(Collectors.toList());

        return reportService.genericReportThreeColumns(genericReport, parameters);

    }

    public void deleteById(String id) throws TechnicalStaffNotFoundException {
        verifyIfExists(id);
        try{
            technicalStaffRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.TECHNICAL_STAFF_DELETE_ERROR.getDescription());
        }
    }

    public PaginateResponseDTO<TechnicalStaffDTO> listAllToForwardTicket(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        String department = Utils.getUser().getDepartmentCode().getId();
        if(term.isBlank())
            term = "";
        return  PageableUtil.paginate( technicalStaffRepository.findToForwardTicket(term.toUpperCase(Locale.ROOT).trim(), department, paging).map(technicalStaffMapper::toDTO));
    }

    public MessageResponseDTO transferTechnicalStaff(TransferTechnicalDTO transferTechnicalDTO) throws TechnicalStaffNotFoundException {
        findById(transferTechnicalDTO.getTechnicianId());
        unityRepository.findById(transferTechnicalDTO.getBarcode()).orElseThrow(()-> new IllegalArgumentException(MessageUtils.UNITY_NOT_FOUND_BY_ID.getDescription() + transferTechnicalDTO.getBarcode()));
        return nasphService.transferTechnicalStaff(transferTechnicalDTO);
    }

    public byte[] emitProof(EmitProofDTO emitProofDTO) throws TechnicalStaffNotFoundException, JRException, IOException {
        TechnicalStaffDTO technicalStaffDTO = findById(emitProofDTO.getTechnicianId());
        if(emitProofDTO.getResponsibleId()!=null && !emitProofDTO.getResponsibleId().isEmpty())
            departmentRepository.findById(emitProofDTO.getResponsibleId()).orElseThrow(()-> new IllegalArgumentException(MessageUtils.DEPARTMENT_NOT_FOUND_BY_ID.getDescription() + emitProofDTO.getResponsibleId()));

        List<EmitProofReportDTO> emitProofReportDTO = technicalStaffRepository.emitProof(emitProofDTO);
        if(emitProofReportDTO== null || emitProofReportDTO.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("matricula", technicalStaffDTO.getId());
        parameters.put("nome", technicalStaffDTO.getTechnicianName());
        parameters.put("lotacao", technicalStaffDTO.getDepartmentCode().getId());
        return reportService.emitProofReport(emitProofReportDTO, parameters);
    }

    public PaginateResponseDTO<TechnicalStaffDTO> listAllByUnity(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        String department = Utils.getUser().getDepartmentCode().getId();
        if(term.isBlank())
            term = "";
        return  PageableUtil.paginate(technicalStaffRepository.findLikeByUnity(term.toUpperCase(Locale.ROOT).trim(), department, paging).map(technicalStaffMapper::toDTO));
    }
}
