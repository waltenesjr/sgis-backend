package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.ComponentMov;
import br.com.oi.sgis.entity.TicketDiagnosis;
import br.com.oi.sgis.entity.TicketIntervention;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.exception.RepSituationNotFoundException;
import br.com.oi.sgis.exception.TicketInterventionNotFoundException;
import br.com.oi.sgis.mapper.TicketInterventionMapper;
import br.com.oi.sgis.repository.*;
import br.com.oi.sgis.service.factory.TicketInterventionFactory;
import br.com.oi.sgis.service.validator.Validator;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TicketInterventionService {

    private final TicketInterventionRepository ticketInterventionRepository;
    private final RepairTicketRepository repairTicketRepository;
    private final TicketInterventionFactory ticketInterventionFactory;
    private static final TicketInterventionMapper ticketInterventionMapper = TicketInterventionMapper.INSTANCE;
    private final ReportService reportService;
    private final Validator<TicketInterventionDTO> validator;
    private final TicketDiagnosisRepository ticketDiagnosisRepository;
    private final ComponentMovRepository componentMovRepository;
    private final RepSituationService repSituationService;

    private final NasphService nasphService;


    public PaginateResponseDTO<TicketInterventionDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap = TicketInterventionMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, sortMap));
        if(term.isEmpty())
            term = "";
        return PageableUtil.paginate(ticketInterventionRepository.findLike(term.toUpperCase(Locale.ROOT),paging).map(ticketInterventionMapper::toDTO), sortMap);
    }

    public PaginateResponseDTO<TicketInterventionDTO> listAllToClosePaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap = TicketInterventionMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, sortMap));
        if(term.isEmpty())
            term = "";
        return PageableUtil.paginate(ticketInterventionRepository.findLikeToClose(term.toUpperCase(Locale.ROOT),paging).map(ticketInterventionMapper::toDTO), sortMap);
    }

    public PaginateResponseDTO<TicketInterventionDTO> listAllToUpdatePaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term, String brNumber) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap = TicketInterventionMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, sortMap));
        if(term.isEmpty())
            term = "";
        String departmentId = Utils.getUser().getDepartmentCode().getId();
        return PageableUtil.paginate(ticketInterventionRepository.findLikeToUpdate(term.toUpperCase(Locale.ROOT), departmentId, brNumber,paging).map(ticketInterventionMapper::toDTO), sortMap);
    }
    @Transactional(rollbackFor = IllegalArgumentException.class)
    public MessageResponseDTO openInterventionFromUpdate(TicketInterventionDTO ticketInterventionDTO){
        return createTicketIntervention(ticketInterventionDTO);
    }

    private MessageResponseDTO createTicketIntervention(TicketInterventionDTO ticketInterventionDTO) {
        validator.validate(ticketInterventionDTO);
        ticketInterventionDTO.setExternalRepair(false);
        TicketIntervention ticketToSave = ticketInterventionFactory.createTicketIntervention(ticketInterventionDTO);
        try {

            if(ticketToSave.getTicketDiagnosis()!=null)
                ticketToSave.getTicketDiagnosis().forEach(d -> d.getId().setTicketIntervention(ticketToSave));

            ticketInterventionRepository.save(ticketToSave);
            saveDiagnoses(ticketToSave.getTicketDiagnosis(), ticketToSave);
            saveComponents(ticketToSave.getTicketComponents(), ticketToSave);
            return createMessageResponse(MessageUtils.TICKET_INTERV_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.TICKET_INTERV_SAVE_ERROR.getDescription());
        }
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    public MessageResponseDTO openIntervention(TicketInterventionDTO ticketInterventionDTO)  {
        Optional<TicketIntervention> ticketIntervention = ticketInterventionRepository.findByUnityAndTicket(ticketInterventionDTO.getRepairTicket().getBrNumber(),ticketInterventionDTO.getUnity().getId());
        if(ticketIntervention.isPresent()){
            throw new IllegalArgumentException(MessageUtils.TICKET_INTERV_ALREADY_EXISTS.getDescription());
        }
        return createTicketIntervention(ticketInterventionDTO);
    }

    private MessageResponseDTO createMessageResponse(String message, HttpStatus status) {
        return MessageResponseDTO.builder().title("Sucesso!").message(message).status(status).build();
    }

    public TicketInterventionDTO findByIdDTO(TicketInterventionDTO dto) throws TicketInterventionNotFoundException {
        TicketIntervention ticketIntervention = verifyIfExists(dto.getRepairTicket().getBrNumber(), dto.getSequence());
        return ticketInterventionMapper.toDTO(ticketIntervention);
    }
    private TicketIntervention verifyIfExists(String brNumber, Long sequence) throws TicketInterventionNotFoundException {
        return ticketInterventionRepository.findById(brNumber, sequence)
                .orElseThrow(()-> new TicketInterventionNotFoundException(MessageUtils.TICKET_INTERV_NOT_FOUND_BY_ID.getDescription() + brNumber));
    }

    public byte[] technicianReport(String idTechnician) throws JRException, IOException {
        String department = null;
        if(idTechnician == null || idTechnician.isEmpty()) {
            department = Utils.getUser().getDepartmentCode().getId();
            idTechnician = "";
        }
        List<TechnicianTicketDTO> technicianData = ticketInterventionRepository.getTechnicianData(idTechnician, department);
        if(technicianData== null || technicianData.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        Map<String, List<TechnicianTicketDTO>> map = groupByTechnician(technicianData);
        List<TechnicianTicketReportDTO> reportData = new ArrayList<>();
        map.forEach((key, value) -> reportData.add(TechnicianTicketReportDTO.builder().technicianName(value.get(0).getTechnicalName())
                .technicianId(key)
                .technicianTicket(value)
                .build()));
        Map<String, Object> parameters = new HashMap<>();
        return reportService.technicianTicketReport(reportData, parameters);
    }

    private Map<String, List<TechnicianTicketDTO>> groupByTechnician(List<TechnicianTicketDTO> technicians) {
        return technicians.parallelStream()
                .sorted(Comparator.comparing(TechnicianTicketDTO::getTechnicalId,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(groupingBy(TechnicianTicketDTO::getTechnicalId, LinkedHashMap::new,
                        Collectors.toList()));
    }

    public byte[] orderServiceReport(String idTechnician, String barcode) throws JRException, IOException {
        String department = null;
        if((idTechnician == null || idTechnician.isEmpty()) && (barcode == null || barcode.isEmpty())) {
            department = Utils.getUser().getDepartmentCode().getId();
            idTechnician = "";
            barcode = "";
        }
        List<OrderServiceDTO> orderServiceData = ticketInterventionRepository.getOrderServiceData(idTechnician, barcode, department);
        if(orderServiceData== null || orderServiceData.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        return reportService.orderServiceData(orderServiceData, parameters);
    }

    public List<SituationDTO> updateInterventionSituation(String brNumber) throws RepSituationNotFoundException {
        String newSituationId = null;
        try {
            newSituationId = nasphService.updateInterventionSituation(brNumber);
            if(newSituationId!=null)
                return List.of(repSituationService.findById(newSituationId));
        }catch (Exception e){} //Ignora qlqr exceção para mander lista alternativa
        return repSituationService.listForwardRepairFromInterv();
    }

    @Transactional @SneakyThrows
    public MessageResponseDTO updateIntervention(TicketInterventionUpdateDTO ticketInterventionUpdateDTO) {
        Optional<TicketIntervention> ticketIntervention = ticketInterventionRepository.findById(ticketInterventionUpdateDTO.getTicketIntervention().getRepairTicket().getBrNumber(),
                ticketInterventionUpdateDTO.getTicketIntervention().getSequence());
        forwardTicket(ticketInterventionUpdateDTO.getForwardRepair(), ticketInterventionUpdateDTO.getTicketIntervention().getRepairTicket(), ticketInterventionUpdateDTO.getSituationDTO());
        if(ticketIntervention.isEmpty()){
            return openInterventionFromUpdate(ticketInterventionUpdateDTO.getTicketIntervention());
        }
        validator.validate(ticketInterventionUpdateDTO.getTicketIntervention());
        TicketIntervention ticketToSave = ticketInterventionFactory.createTicketIntervention(ticketInterventionUpdateDTO.getTicketIntervention());
        updateDiagnoses(ticketToSave);
        updateComponents(ticketToSave);
        try{
            ticketInterventionRepository.save(ticketToSave);
            return createMessageResponse(MessageUtils.TICKET_INTERV_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.TICKET_INTERV_UPDATE_ERROR.getDescription());
        }

    }

    @Transactional @SneakyThrows
    public MessageResponseDTO closeIntervention(TicketInterventionUpdateDTO ticketInterventionUpdateDTO) {
        TicketIntervention ticketToClose = ticketInterventionMapper.toModel(ticketInterventionUpdateDTO.getTicketIntervention());
        forwardTicket(ticketInterventionUpdateDTO.getForwardRepair(), ticketInterventionUpdateDTO.getTicketIntervention().getRepairTicket(), ticketInterventionUpdateDTO.getSituationDTO());
        updateDiagnoses(ticketToClose);
        updateComponents(ticketToClose);
        try{
            ticketInterventionRepository.closeIntervention(ticketToClose.getId().getSequence(), ticketToClose.getId().getRepairTicket().getId(), LocalDateTime.now());
            return createMessageResponse(MessageUtils.TICKET_INTERV_CLOSE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.TICKET_INTERV_CLOSE_ERROR.getDescription());
        }
    }

    private void forwardTicket(Boolean forwardRepair, RepairTicketDTO repairTicket, SituationDTO situationDTO) {
        if(Boolean.TRUE.equals(forwardRepair)) {
            try {
                repairTicketRepository.updateSituation(repairTicket.getBrNumber(), situationDTO.getId());
            } catch (Exception e) {
                throw new IllegalArgumentException(MessageUtils.TICKET_INTERV_UPDATE_FORWARD_ERROR.getDescription());
            }
        }
    }

    private void saveDiagnoses(List<TicketDiagnosis> ticketDiagnoses, TicketIntervention ticketToSave) {
        try {
            if(ticketDiagnoses !=null && !ticketDiagnoses.isEmpty()) {
                ticketDiagnoses.forEach(d -> d.getId().setTicketIntervention(ticketToSave));
                ticketDiagnosisRepository.saveAll(ticketDiagnoses);
            }
        }catch (RuntimeException e){
            throw new IllegalArgumentException(MessageUtils.TICKET_DIAGNOSIS_SAVE_ERROR.getDescription());
        }
    }

    private void updateDiagnoses(TicketIntervention ticketIntervention) throws TicketInterventionNotFoundException {
        List<TicketDiagnosis> ticketDiagnosis = ticketIntervention.getTicketDiagnosis();
        List<TicketDiagnosis> oldTicketDiagnosis = verifyIfExists(ticketIntervention.getId().getRepairTicket().getId(),
                ticketIntervention.getId().getSequence()).getTicketDiagnosis();
        if(ticketDiagnosis.isEmpty() && oldTicketDiagnosis.isEmpty())
            return;
        if(oldTicketDiagnosis.isEmpty()) {
            saveDiagnoses(ticketDiagnosis, ticketIntervention);
        }
        else if(ticketDiagnosis.isEmpty()){
            ticketDiagnosisRepository.deleteAll(oldTicketDiagnosis);
        }else {
            ticketDiagnosisRepository.deleteAll(oldTicketDiagnosis);
            saveDiagnoses(ticketDiagnosis, ticketIntervention);
        }
    }

    private void saveComponents(List<ComponentMov> componentMovs, TicketIntervention ticketToSave) {
        if(componentMovs ==null || componentMovs.isEmpty())
            return;
        try {
            for (ComponentMov c : componentMovs) {
                c.setTicketIntervention(ticketToSave);
                if (componentMovRepository.findById(c.getId()).isPresent()) {
                    componentMovRepository.updateQuantity(c.getQuantity(), c.getId().getDepartmentComponent().getId(), c.getId().getSequence());
                } else {
                    createComponent(c, ticketToSave.getId().getRepairTicket().getId(), ticketToSave.getId().getSequence());
                }}
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.TICKET_COMPONENT_SAVE_ERROR.getDescription());
        }
    }

    private void createComponent(ComponentMov c, String brNumber, Long sequence) {
        Long nextSequence = componentMovRepository.getNextSequence();
        String departmentId = c.getId().getDepartmentComponent().getId().getDepartment().getId();
        String componentId = c.getId().getDepartmentComponent().getId().getComponent().getId();
        componentMovRepository.saveComponent(componentId, departmentId, Utils.getUser().getId(),
                c.getQuantity(), brNumber, sequence, nextSequence);
    }

    private void updateComponents(TicketIntervention ticketIntervention) throws TicketInterventionNotFoundException {
        List<ComponentMov> componentMov = ticketIntervention.getTicketComponents();
        List<ComponentMov> oldComponentMov = verifyIfExists(ticketIntervention.getId().getRepairTicket().getId(),
                ticketIntervention.getId().getSequence()).getTicketComponents();
        if(componentMov.isEmpty() && oldComponentMov.isEmpty())
            return;
        if(oldComponentMov.isEmpty()) {
            saveComponents(componentMov, ticketIntervention);
        }
        else if(componentMov.isEmpty()){
            oldComponentMov.forEach(c-> c.setQuantity(0L));
            saveComponents(oldComponentMov, ticketIntervention);
        }else {
            List<ComponentMov> removed = new ArrayList<>();
            oldComponentMov.forEach(o -> {
                AtomicInteger count = new AtomicInteger();
                componentMov.forEach(c ->{
                    if(c.getId().getDepartmentComponent().getId().getComponent().getId().equals(o.getId().getDepartmentComponent().getId().getComponent().getId())){
                        count.getAndIncrement();
                    }
                });
                if(count.get() == 0)
                    removed.add(o);
            });
            removed.forEach(c-> c.setQuantity(0L));
            saveComponents(removed, ticketIntervention);
            saveComponents(componentMov, ticketIntervention);
        }
    }

    public byte[] technicianTimesReport(TechnicianTimesFilterDTO filterDTO) {
        if(filterDTO.getInitialDate()!=null)
            Utils.isPeriodInvalid(filterDTO.getInitialDate(), filterDTO.getFinalDate());
        List<TechnicianTimesDTO> technicianTimes = ticketInterventionRepository.findTechnicianTimes(filterDTO);
        if(technicianTimes.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        try {
            Map<String, Object> parameters = new HashMap<>();
            List<TechnicianTimesReportDTO> reportData = groupTechnicianTimesByTechnician(technicianTimes);
            return reportService.technicianTimesReport(reportData, parameters);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

    private  List<TechnicianTimesReportDTO> groupTechnicianTimesByTechnician(List<TechnicianTimesDTO> technicianTimesDTOS) {
        List<TechnicianTimesReportDTO> reportData = new ArrayList<>();
        Map<String, List<TechnicianTimesDTO>> mapValues =  technicianTimesDTOS.parallelStream().collect(groupingBy(TechnicianTimesDTO::getTechnician, LinkedHashMap::new,
                Collectors.toList()));
        mapValues.forEach((key, value) -> {
            long totalHours = value.stream().mapToLong(TechnicianTimesDTO::getHours).sum();
            long totalMinutes = value.stream().mapToLong(TechnicianTimesDTO::getMinutes).sum();
            long totalSeconds = value.stream().mapToLong(TechnicianTimesDTO::getSeconds).sum();
            TechnicianTimesReportDTO technicianTimesReportDTO = TechnicianTimesReportDTO.builder()
                    .items(value)
                    .technician(key).technicianName(value.get(0).getTechnicianName()).department(value.get(0).getDepartmentTechnician())
                    .totalHours(totalHours).totalMinutes(totalMinutes).totalSeconds(totalSeconds)
                    .build();
            reportData.add(technicianTimesReportDTO);
        });
        return reportData;
    }
}
