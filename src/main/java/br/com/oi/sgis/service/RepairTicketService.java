package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.*;
import br.com.oi.sgis.entity.view.WarrantyView;
import br.com.oi.sgis.enums.SituationEnum;
import br.com.oi.sgis.exception.RepSituationNotFoundException;
import br.com.oi.sgis.exception.RepairTicketException;
import br.com.oi.sgis.mapper.RepairTicketMapper;
import br.com.oi.sgis.mapper.StationMapper;
import br.com.oi.sgis.mapper.UnityMapper;
import br.com.oi.sgis.repository.*;
import br.com.oi.sgis.service.factory.RepairTicketFactory;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.RoundingMode.HALF_UP;
import static java.util.stream.Collectors.groupingBy;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RepairTicketService {

    private final UnityService unityService;
    private final RepSituationService repSituationService;
    private final TicketRepExtraInfoService ticketRepExtraInfoService;
    private final BarcodeFASRepository barcodeFASRepository;
    private final RepairTicketRepository repairTicketRepository;
    private final RepairTicketFactory repairTicketFactory;
    private final EquipmentTypeRepairRepository equipmentTypeRepairRepository;
    private final Validator<RepairTicket> validator;
    private final DepartmentUnityRepository departmentUnityRepository;
    private final NasphService nasphService;
    private final ReportService reportService;
    private final RepairTicketAnalysisService repairTicketAnalysisService;
    private final WarrantyViewRepository warrantyViewRepository;
    private final CostComparisonService costComparisonService;
    private final ProductivityComparisonRepositoryCustom productivityComparisonRepository;

    private static final RepairTicketMapper repairTicketMapper = RepairTicketMapper.INSTANCE;


    public UnityDTO getUnity(String id) {
        UnityDTO unity =  unityService.findById(id);
        if(unity.getSituationCode() != null && unity.getSituationCode().getId().equals(SituationEnum.REP.getCod()))
            throw new IllegalArgumentException(MessageUtils.UNITY_ALREADY_REP_SITUATION_ERROR.getDescription());

        if(unity.getSituationCode() != null && !SituationEnum.canOpenRepair().contains(SituationEnum.valueOf(unity.getSituationCode().getId())))
            throw new IllegalArgumentException(MessageUtils.UNITY_SITUATION_REPAIR_ERROR.getDescription());

        return unity;
    }

    public SituationDTO getDefaultRepSituation() throws RepSituationNotFoundException {
        return repSituationService.findById("ABE");
    }

    public MessageResponseDTO fasNumber(String fasNumber){

        if(fasNumber == null || fasNumber.isEmpty())
            return createMessageResponse("Atenção", MessageUtils.UNITY_BLANK_FAS_N_REPAIR.getDescription(), HttpStatus.NO_CONTENT);

        Optional<BarcodeFAS> barcodeFAS =  barcodeFASRepository.findById(fasNumber);

        if(barcodeFAS.isPresent()){
            return createMessageResponse(null, MessageUtils.UNITY_EXISTS_FAS_N_REPAIR.getDescription(), HttpStatus.OK);
        }else {
            return createMessageResponse("Atenção", MessageUtils.UNITY_NOT_EXISTS_FAS_N_REPAIR.getDescription(), HttpStatus.NO_CONTENT);
        }
    }

    private MessageResponseDTO createMessageResponse(String title, String message, HttpStatus status) {
        if(title==null)
            title = "Sucesso!";
        return MessageResponseDTO.builder().message(message).title(title).status(status).build();
    }

    @SneakyThrows
    public MessageResponseDTO createRepairTicket(RepairTicketDTO repairTicketDTO){
        RepairTicket repairTicketToSave = repairTicketFactory.createRepairTicket(repairTicketDTO);
        return createMessageResponse(null,MessageUtils.REPAIR_TICKET_SUCCESS.getDescription() + repairTicketToSave.getId(),
                HttpStatus.CREATED);
    }

    public PaginateResponseDTO<Object> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap = RepairTicketMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, sortMap));
        if (term.isBlank())
            return PageableUtil.paginate(repairTicketRepository.findAll(paging).map(repairTicketMapper::toDTO), sortMap);

        return PageableUtil.paginate(repairTicketRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(repairTicketMapper::toDTO), sortMap);

    }

    @SneakyThrows
    public MessageResponseDTO updateRepairTicket(RepairTicketDTO repairTicketDTO){
        UnityDTO unity =  unityService.findById(repairTicketDTO.getUnityId());
        RepairTicket repairTicketToUpdate = repairTicketMapper.toModel(repairTicketDTO);
        UnityMapper unityMapper = UnityMapper.INSTANCE;
        repairTicketToUpdate.setUnity(unityMapper.toModel(unity));
        List<Integer> userLevels = Utils.getUser().getLevels().stream().map(Level::getLvl).collect(Collectors.toList());

        validator.validate(repairTicketToUpdate);

        if(!unity.getResponsible().getId().equals(Utils.getUser().getDepartmentCode().getId())
                && !userLevels.contains(0))
            throw new IllegalArgumentException(MessageUtils.REPAIR_TICKET_UPDT_ERROR.getDescription());

        if(!List.of(SituationEnum.REP.getCod(), SituationEnum.TRR.getCod()).contains(unity.getSituationCode().getId())&&
                !userLevels.contains(0))
            throw new IllegalArgumentException(MessageUtils.REPAIR_TICKET_SITUATION_UPDT_ERROR.getDescription());

        repairTicketRepository.save(repairTicketToUpdate);

        return createMessageResponse("Alteração realizada com sucesso!",MessageUtils.REPAIR_TICKET_UPDATE_SUCCESS.getDescription() + repairTicketToUpdate.getId(),
                HttpStatus.OK);
    }

    public TicketRepExtraInfoDTO getExtraInformation(String id) throws RepairTicketException {
        RepairTicket repairTicket = verifyIfExists(id);
        return ticketRepExtraInfoService.getExtraInfo(repairTicket);
    }

    @SneakyThrows
    public RepairTicketDTO findById(String id)  throws RepairTicketException {
        RepairTicket repairTicket = verifyIfExists(id);
        return repairTicketMapper.toDTO(repairTicket);
    }

    private RepairTicket verifyIfExists(String id) throws RepairTicketException {
        return repairTicketRepository.findById(id)
                .orElseThrow(()-> new RepairTicketException(MessageUtils.REPAIR_TICKET_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public AcceptTicketRepairDTO getAcceptRepair(String id) {
        UnityDTO unity =  unityService.findById(id);
        verifyIfExistsByUnityAndSituation(id);
        DepartmentUnity departmentUnity = getDepartmentUnity(unity);

        StationMapper stationMapper = StationMapper.INSTANCE;
        if(departmentUnity!=null)
            return AcceptTicketRepairDTO.builder()
                    .stationDTO(stationMapper.toDTO(departmentUnity.getStation()))
                    .location(departmentUnity.getLocation())
                    .unityId(unity.getId()).build();

        return AcceptTicketRepairDTO.builder()
                .unityId(unity.getId()).stationDTO(unity.getStation()).location(unity.getLocation()).build();
    }

    @SneakyThrows
    private void verifyIfExistsByUnityAndSituation(String id) {
        if(repairTicketRepository.findTopByUnityIdAndSituationId(id, "ABE").isEmpty()){
            throw  new RepairTicketException(MessageUtils.REPAIR_TICKET_OPEN_NOT_FOUND_BY_UNITY.getDescription());
        }
    }

    private DepartmentUnity getDepartmentUnity(UnityDTO unity) {
        Department department = Department.builder().id(Utils.getUser().getDepartmentCode().getId()).build();
        AreaEquipament areaEquipament = AreaEquipament.builder().id(unity.getUnityCode().getId()).build();
        DepartmentUnityID duId = DepartmentUnityID.builder().department(department).equipament(areaEquipament).build();
        return departmentUnityRepository.findById(duId).orElse(null);
    }

    public ResponseEntity<MessageResponseDTO> verifyWarrantyDate(String unityId) {
        UnityDTO unity = unityService.findById(unityId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        MessageResponseDTO body;
        if (unity.getWarrantyDate()!=null) {
            body = createMessageResponse("Info", String.format("Item %s em garantia até %s", unityId, unity.getWarrantyDate().format(formatter)), HttpStatus.CONTINUE);
            return ResponseEntity.accepted().body(body);
        }
        return ResponseEntity.noContent().build();
    }

    public MessageResponseDTO acceptRepair(AcceptTicketRepairDTO acceptTicketRepairDTO) {
        AcceptTcktRepairNasphDTO repairNasphDTO = AcceptTcktRepairNasphDTO.builder()
                .unity(acceptTicketRepairDTO.getUnityId())
                .location(acceptTicketRepairDTO.getLocation())
                .user(Utils.getUser().getId()).build();

        if(acceptTicketRepairDTO.getStationDTO()!= null)
            repairNasphDTO.setStation(acceptTicketRepairDTO.getStationDTO().getId());

        return nasphService.acceptRepairTicket(repairNasphDTO);

    }

    public PaginateResponseDTO<RepairTicketDTO> getForwardRepair(ForwardTicketDTO forwardTicketDTO, Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap = RepairTicketMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, sortMap));
        String department = Utils.getUser().getDepartmentCode().getId();
        if(forwardTicketDTO.getInitialDate() != null ) {
            Utils.isPeriodInvalid(forwardTicketDTO.getInitialDate(), forwardTicketDTO.getFinalDate());
            return PageableUtil.paginate(repairTicketRepository.findTicketsToForwardWithDateFilter(forwardTicketDTO, department, paging).map(repairTicketMapper::toDTO), sortMap);
        }
        return PageableUtil.paginate(repairTicketRepository.findTicketsToForward(forwardTicketDTO, department, paging).map(repairTicketMapper::toDTO), sortMap);
    }

    public RepairTicketDTO ticketToForward(String brNumber) throws RepairTicketException {
        RepairTicketDTO repairTicketDTO = findById(brNumber);
        if(repairTicketRepository.findForward(brNumber) > 0){
            throw new IllegalArgumentException(MessageUtils.INVALID_FORWARD_TICKET.getDescription());
        }
        return repairTicketDTO;
    }

    public MessageResponseDTO forwardRepair(RepairTicketDTO repairTicketDTO) {
        try {
            repairTicketRepository.forwardRepair(repairTicketDTO.getMaintainer().getId(),
                    repairTicketDTO.getRepairTechnician().getId(), repairTicketDTO.getContract().getId(),
                    repairTicketDTO.getSituation().getId(), repairTicketDTO.getBrNumber());

            return createMessageResponse(null, MessageUtils.REPAIR_TICKET_FORWARD_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (RuntimeException e){
            throw new IllegalArgumentException(MessageUtils.REPAIR_TICKET_FORWARD_ERROR.getDescription());
        }
    }

    public byte[] forwardTicketReport(ForwardTicketDTO forwardTicketDTO, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<RepairTicketDTO> forwardTickets = getForwardRepair(forwardTicketDTO,  0, Integer.MAX_VALUE, sortAsc, sortDesc).getData();
        if(forwardTickets== null || forwardTickets.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        Map<String, Object> parameters = new HashMap<>();
        List<ForwardTicketReportDTO> reportData = new ArrayList<>();
        String none =  "NENHUM";
        forwardTickets.forEach(t ->{
            ForwardTicketReportDTO f =  ForwardTicketReportDTO.builder().contract(t.getContract()!=null ? t.getContract().getId() : none)
                    .modelUnity(t.getUnity().getUnityCode().getId())
                    .maintainer(t.getMaintainer()!=null ? t.getMaintainer().getId() : none)
                    .technician(t.getRepairTechnician()!=null ? t.getRepairTechnician().getId() : none)
                    .unityId(t.getUnityId())
                    .situation(t.getSituation().getId()).build();
            reportData.add(f);
        });
        return reportService.forwardTicketReport(reportData, parameters);
    }

    public MessageResponseDTO closeRepair(CloseRepairTickectDTO closeRepairTickectDTO) {
        unityService.findById(closeRepairTickectDTO.getBarcode());
        closeRepairTickectDTO.setUserId(Utils.getUser().getId());
        return nasphService.closeRepair(closeRepairTickectDTO);
    }

    public SituationDTO closeRepairSituation(String unityId) throws RepSituationNotFoundException {
        unityService.findById(unityId);
        String newSituationId = nasphService.closeRepairSituation(unityId);
        if(newSituationId == null)
            throw new IllegalArgumentException(MessageUtils.INVALID_CLOSE_SITUATION.getDescription());
        SituationDTO situationDTO = repSituationService.findById(newSituationId);
        return situationDTO;
    }

    public MessageResponseDTO devolutionRepair(DevolutionRepairTicketDTO devolutionRepairTicketDTO) {
        unityService.findById(devolutionRepairTicketDTO.getBarcode());

        devolutionRepairTicketDTO.setUserId(Utils.getUser().getId());
        return nasphService.devolutionRepair(devolutionRepairTicketDTO);
    }

    public MessageResponseDTO cancelRepair(String unityId) {
        unityService.findById(unityId);
        return nasphService.cancelRepair(unityId, Utils.getUser().getId());
    }

    public PaginateResponseDTO<RepairTicketDTO> getDesignateTechnician(DesignateTechnicianDTO designateTechnicianDTO, Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap = RepairTicketMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, sortMap));
        String department = Utils.getUser().getDepartmentCode().getId();
        return PageableUtil.paginate(repairTicketRepository.findDesignateTechnician(designateTechnicianDTO, department, paging).map(repairTicketMapper::toDTO), sortMap);

    }

    public MessageResponseDTO designateTechnician(RepairTicketDTO repairTicketDTO) {
        try {
            repairTicketRepository.designateTechnician(
                    repairTicketDTO.getRepairTechnician().getId(), repairTicketDTO.getUnityId(),
                    repairTicketDTO.getBrNumber());

            return createMessageResponse(null, MessageUtils.REPAIR_TICKET_DESIG_TECH_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (RuntimeException e){
            throw new IllegalArgumentException(MessageUtils.REPAIR_TICKET_DESIG_TECH_ERROR.getDescription());
        }
    }

    public PaginateResponseDTO<RepairTicketDTO> getTicketsForIntervention(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap = RepairTicketMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, sortMap));
        if (term.isBlank())
            term= "";

        String departmentId = Utils.getUser().getDepartmentCode().getId();
        return PageableUtil.paginate(repairTicketRepository.findTicketsForInterventionLike(term.toUpperCase(Locale.ROOT).trim(), departmentId, paging).map(repairTicketMapper::toDTO), sortMap);

    }

    public byte[] repairSummaryReport(RepairSummaryFilterDTO repairSummaryFilterDTO) throws JRException, IOException {
        if(repairSummaryFilterDTO.isQuantity())
            return summaryQuantity(repairSummaryFilterDTO);
        return summaryValues(repairSummaryFilterDTO);
    }

    private byte[] summaryValues(RepairSummaryFilterDTO repairSummaryFilterDTO) throws JRException, IOException {
        List<RepairSummaryValueDTO> summaryDTOS = repairTicketRepository.getValuesSummary(repairSummaryFilterDTO.getRepairCenter(),
                repairSummaryFilterDTO.getInitialDate(), repairSummaryFilterDTO.getFinalDate());
        if(summaryDTOS== null || summaryDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        Map<String, Object> parameters = new HashMap<>();
        try {
            List<RepairSummaryReportDTO> reportData = groupByValue(summaryDTOS);
            return reportService.summaryValueRepairReport(reportData, parameters);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

    private byte[] summaryQuantity(RepairSummaryFilterDTO repairSummaryFilterDTO) throws JRException, IOException {
        List<RepairSummaryQuantityDTO> summaryDTOS = repairTicketRepository.getQuantitySummary( repairSummaryFilterDTO.getRepairCenter(),
                repairSummaryFilterDTO.getInitialDate(), repairSummaryFilterDTO.getFinalDate());
        if(summaryDTOS== null || summaryDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        Map<String, Object> parameters = new HashMap<>();
        try {
            List<RepairSummaryReportDTO> reportData = groupByQuantity(summaryDTOS);
            return reportService.summaryQuantityRepairReport(reportData, parameters);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

    private  List<RepairSummaryReportDTO> groupByValue(List<RepairSummaryValueDTO> values) {
        List<RepairSummaryReportDTO> reportData = new ArrayList<>();
        values.parallelStream().forEach(q -> q.setOpen(repairTicketRepository.openTicketsByMonthAndYear(q.getMonthInt(), q.getYear())));
        LinkedHashMap<String, List<RepairSummaryValueDTO>> mapValues =  values.parallelStream()
                .collect(groupingBy(RepairSummaryValueDTO::getRepairCenter, LinkedHashMap::new,
                        Collectors.toList()));
        mapValues.forEach((key, value) -> {
            RepairSummaryReportDTO reportDTO = RepairSummaryReportDTO.builder()
                    .valueItems(value)
                    .repairCenter(key)
                    .build();
            reportData.add(reportDTO);
        });
        return reportData;
    }

    private  List<RepairSummaryReportDTO> groupByQuantity(List<RepairSummaryQuantityDTO> values) {
        List<RepairSummaryReportDTO> reportData = new ArrayList<>();
        values.parallelStream().forEach(q -> q.setOpen(repairTicketRepository.openTicketsByMonthAndYear(q.getMonthInt(), q.getYear())));
        LinkedHashMap<String, List<RepairSummaryQuantityDTO>> mapValues =  values.parallelStream()
                .collect(groupingBy(RepairSummaryQuantityDTO::getRepairCenter, LinkedHashMap::new,
                        Collectors.toList()));
        mapValues.forEach((key, value) -> {
            RepairSummaryReportDTO reportDTO = RepairSummaryReportDTO.builder()
                    .quantityItems(value)
                    .repairCenter(key)
                    .build();
            reportData.add(reportDTO);
        });
        return reportData;
    }

    public byte[] analyticReport(RepairAnalyticFilterDTO filterDto) {
        if (filterDto.getInitialRepairDate() != null)
            Utils.isPeriodInvalid(filterDto.getInitialRepairDate(), filterDto.getFinalRepairDate());
        if (filterDto.getInitialWarrantyDate() != null)
            Utils.isPeriodInvalid(filterDto.getInitialWarrantyDate(), filterDto.getFinalWarrantyDate());
        List<AnalyticRepairDTO> analyticRepairDTOS = repairTicketRepository.getAnalytics(filterDto);
        if (analyticRepairDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        try {
            return repairTicketAnalysisService.report(analyticRepairDTOS, filterDto.getBreakTotals());
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

    public byte[] externalRepairReport(ExternalRepairFilterDTO filterDto) {
        verifyPeriods(filterDto);
        List<ExternalRepairReportDTO> externalRepairReportDTOS = repairTicketRepository.getExternalRepair(filterDto);
        if (externalRepairReportDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        try {
            externalRepairReportDTOS.parallelStream().forEach(er-> {
                WarrantyView warrantyView = warrantyViewRepository.findOneByUnityId(er.getBarcode());
                if (warrantyView != null) {
                    er.setContract(warrantyView.getContract());
                    er.setWarrantyDate(warrantyView.getWarrantyDate());
                    er.setBrWarranty(warrantyView.getBrNumber());
                    er.setWarrantyProvider(warrantyView.getProvider());
                }
            });
            return reportService.externalRepairReport(externalRepairReportDTOS);
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

    private void verifyPeriods(ExternalRepairFilterDTO filterDto) {
        if (filterDto.getInitialRepairDate() != null)
            Utils.isPeriodInvalid(filterDto.getInitialRepairDate(), filterDto.getFinalRepairDate());
        if (filterDto.getInitialWarrantyDate() != null)
            Utils.isPeriodInvalid(filterDto.getInitialWarrantyDate(), filterDto.getFinalWarrantyDate());
        if (filterDto.getInitialEstimateDate() != null)
            Utils.isPeriodInvalid(filterDto.getInitialEstimateDate(), filterDto.getFinalEstimateDate());
        if (filterDto.getInitialExitDate() != null)
            Utils.isPeriodInvalid(filterDto.getInitialExitDate(), filterDto.getFinalExitDate());
        if (filterDto.getInitialAcceptanceDate() != null)
            Utils.isPeriodInvalid(filterDto.getInitialAcceptanceDate(), filterDto.getFinalAcceptanceDate());
        if (filterDto.getInitialArriveDate() != null)
            Utils.isPeriodInvalid(filterDto.getInitialArriveDate(), filterDto.getFinalArriveDate());
    }

    public byte[] operatorTicketReport(OperatorTicketFilterDTO filterDto) {
        if (filterDto.getInitialRepairDate() != null)
            Utils.isPeriodInvalid(filterDto.getInitialRepairDate(), filterDto.getFinalRepairDate());
        List<OperatorTicketDTO> operatorTicketDTOS = repairTicketRepository.getOperatorTicket(filterDto);
        if (operatorTicketDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        try {
            List<OperatorTicketReportDTO> reportData = groupOperatorTicket(operatorTicketDTOS);
            return reportService.operatorTicketReport(reportData);
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

    private List<OperatorTicketReportDTO> groupOperatorTicket(List<OperatorTicketDTO> operatorTicketDTOS) {
        List<OperatorTicketReportDTO> reportData = new ArrayList<>();
        LinkedHashMap<String, List<OperatorTicketDTO>> mapValues =  operatorTicketDTOS.parallelStream()
                .collect(groupingBy(OperatorTicketDTO::getCompany, LinkedHashMap::new,
                        Collectors.toList()));
        mapValues.forEach((key, value) -> {
            OperatorTicketReportDTO reportDTO = OperatorTicketReportDTO.builder()
                    .items(value)
                    .company(key)
                    .companyName(value.get(0).getCompanyName())
                    .totalItems(value.size())
                    .build();
            reportData.add(reportDTO);
        });
        return reportData;
    }

    public byte[] ticketReleasedForReturnReport(TicketReleasedFilterDTO filterDto) {
        if (filterDto.getInitialOpenDate() != null)
            Utils.isPeriodInvalid(filterDto.getInitialOpenDate(), filterDto.getFinalOpenDate());
        if (filterDto.getInitialCloseDate() != null)
            Utils.isPeriodInvalid(filterDto.getInitialCloseDate(), filterDto.getFinalCloseDate());
        List<TicketReleasedDTO> ticketReleased = repairTicketRepository.getTicketReleased(filterDto);
        if (ticketReleased.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        try {
            return reportService.ticketReleasedForReturnReport(ticketReleased);
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

    public byte[] ticketForwardedReport(TicketForwardedFilterDTO filterDto) {
        if (filterDto.getInitialDate() != null)
            Utils.isPeriodInvalid(filterDto.getInitialDate(), filterDto.getFinalDate());
        List<String> situations = filterDto.getCondition()!=null? filterDto.getCondition().getSituations() : List.of();
        List<TicketForwardedDTO> ticketsForwarded = repairTicketRepository.getTicketForwarded(filterDto, situations);
        if (ticketsForwarded.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        try {
            List<TicketForwardedReportDTO> reportData = groupTicketForwarded(ticketsForwarded);
            return reportService.ticketForwardedReport(reportData);
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

    private List<TicketForwardedReportDTO> groupTicketForwarded(List<TicketForwardedDTO> ticketsForwarded) {
        List<TicketForwardedReportDTO> reportData = new ArrayList<>();
        LinkedHashMap<String, List<TicketForwardedDTO>> mapValues =  ticketsForwarded.parallelStream()
                .collect(groupingBy(TicketForwardedDTO::getSituationRepair, LinkedHashMap::new,
                        Collectors.toList()));
        mapValues.forEach((key, value) -> {
            TicketForwardedReportDTO reportDTO = TicketForwardedReportDTO.builder()
                    .items(value)
                    .situation(key)
                    .situationDescription(value.get(0).getSituationDescription())
                    .totalItems(value.size())
                    .build();
            reportData.add(reportDTO);
        });
        return reportData;
    }

    public byte[] averageTimeReport(AverageTimeFilterDTO filterDto) {
        if (filterDto.getInitialDate() != null)
            Utils.isPeriodInvalid(filterDto.getInitialDate(), filterDto.getFinalDate());
        List<AverageTimeDTO> averageTime = repairTicketRepository.getAverageTime(filterDto);
        if (averageTime.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        try {
            List<AverageTimeReportDTO> reportData = groupTicketAverageTime(averageTime);
            return reportService.averageTimeRepairReport(reportData);
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

    private List<AverageTimeReportDTO> groupTicketAverageTime(List<AverageTimeDTO> averageTime) {
        List<AverageTimeReportDTO> reportData = new ArrayList<>();
        LinkedHashMap<String, List<AverageTimeDTO>> mapValues =  averageTime.parallelStream()
                .collect(groupingBy(AverageTimeDTO::getUnityCode, LinkedHashMap::new,
                        Collectors.toList()));
        mapValues.forEach((key, value) -> {
            AverageTimeReportDTO reportDTO = AverageTimeReportDTO.builder()
                    .items(value)
                    .unityCode(key)
                    .description(value.get(0).getDescription())
                    .totalItems(value.size())
                    .totalMinutes(value.stream().mapToLong(AverageTimeDTO::getTotalDurationInMinutes).sum())
                    .build();
            reportData.add(reportDTO);
        });
        return reportData;
    }

    public byte[] summaryEquipmentReport(String repairCenter, LocalDateTime initialDate, LocalDateTime finalDate) {
        if (initialDate != null)
            Utils.isPeriodInvalid(initialDate, finalDate);
        List<EquipamentTypeRepairDTO> equipamentTypeRepairDTOS = equipmentTypeRepairRepository.findByRepairCenter(repairCenter, initialDate, finalDate);
        if (equipamentTypeRepairDTOS.isEmpty()) {
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        }
        try {
            List<EquipTypeRepairReportDTO> reportData = groupSummaryEquipment(equipamentTypeRepairDTOS);
            return reportService.summaryEquipmentReport(reportData);
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

    private List<EquipTypeRepairReportDTO> groupSummaryEquipment(List<EquipamentTypeRepairDTO> equipamentTypeRepairDTOS) {
        List<EquipTypeRepairReportDTO> reportData = new ArrayList<>();
        LinkedHashMap<String, List<EquipamentTypeRepairDTO>> mapValues =  equipamentTypeRepairDTOS.parallelStream()
                .collect(groupingBy(EquipamentTypeRepairDTO::getRepairCenter, LinkedHashMap::new,
                        Collectors.toList()));
        mapValues.forEach((key, value) -> {
            EquipTypeRepairReportDTO reportDTO = EquipTypeRepairReportDTO.builder()
                    .items(value)
                    .repairCenter(key)
                    .build();
            reportData.add(reportDTO);
        });
        return reportData;
    }

    public byte[] openRepairReport(OpenRepairFilterDTO filterDto) {
        if (filterDto.getInitialDate() != null)
            Utils.isPeriodInvalid(filterDto.getInitialDate(), filterDto.getFinalDate());
        List<OpenRepairDTO> openRepairs = repairTicketRepository.getOpenRepairs(filterDto);
        if (openRepairs.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        try {
            List<OpenRepairReportDTO> reportData = groupOpenReport(openRepairs);
            return reportService.openRepairReport(reportData);
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

    private List<OpenRepairReportDTO> groupOpenReport(List<OpenRepairDTO> openRepairs) {
        List<OpenRepairReportDTO> reportData = new ArrayList<>();
        LinkedHashMap<String, List<OpenRepairDTO>> mapValues =  openRepairs.parallelStream()
                .collect(groupingBy(OpenRepairDTO::getRepairCenter, LinkedHashMap::new,
                        Collectors.toList()));
        mapValues.forEach((key, value) -> {
            OpenRepairReportDTO reportDTO = OpenRepairReportDTO.builder()
                    .items(value)
                    .repairCenter(key)
                    .build();
            reportData.add(reportDTO);
        });
        return reportData;
    }

    public byte[] costComparisonRepairReport(CostComparisonRepairFilterDTO filterDto) throws JRException, IOException {
        if (filterDto.getInitialDate() != null)
            Utils.isPeriodInvalid(filterDto.getInitialDate(), filterDto.getFinalDate());
        
        return costComparisonService.report(filterDto);
    }

    public byte[] productivityComparisonReport(ProductivityComparisonFilterDTO filterDTO){
        if (filterDTO.getInitialDate() != null)
            Utils.isPeriodInvalid(filterDTO.getInitialDate(), filterDTO.getFinalDate());
        List<ProductivityComparisonDTO> productivityComparisonDTOs =
                productivityComparisonRepository.findProductivityComparisonByTechnical(filterDTO.getRepairCenter(),
                        filterDTO.getTechnicalStaffName(), filterDTO.getInitialDate(), filterDTO.getFinalDate());
        if (productivityComparisonDTOs.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        try {
            List<ProductivityComparisonReportDTO> reportData = groupProductivityComparisonReport(productivityComparisonDTOs);
            Map<String, Object> parameters  =  getParametersProductivityCompReport(reportData);
            return reportService.productivityComparisonReport(reportData, parameters);
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

    private  Map<String, Object> getParametersProductivityCompReport(List<ProductivityComparisonReportDTO> reportData) {
        Map<String, Object> parameters = new HashMap<>();
        Integer total = reportData.parallelStream().mapToInt(ProductivityComparisonReportDTO::getTotalItens).sum();
        BigDecimal spentTotal = reportData.stream().map(ProductivityComparisonReportDTO::getSpentTimeTotal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal spentAverage = spentTotal.divide(BigDecimal.valueOf(total), 2 , HALF_UP);
        BigDecimal repairTimeTotal = reportData.stream().map(ProductivityComparisonReportDTO::getRepairTimeTotal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal repairTimeTotalAvg = repairTimeTotal.divide(BigDecimal.valueOf(total), 2 , HALF_UP);
        BigDecimal repairValueTotal = reportData.stream().map(ProductivityComparisonReportDTO::getRepairValueTotal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal repairValueTotalAverage = repairValueTotal.divide(BigDecimal.valueOf(total), 2 , HALF_UP);
        BigDecimal averageExtTimeTotal = reportData.stream().map(ProductivityComparisonReportDTO::getAverageExtTimeTotal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO)
                .divide(BigDecimal.valueOf(total), 2 , HALF_UP);
        BigDecimal averageExtValueTotal = reportData.stream().map(ProductivityComparisonReportDTO::getAverageExtValueTotal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO)
                .divide(BigDecimal.valueOf(total), 2 , HALF_UP);
        BigDecimal averageIntValueTotal = reportData.stream().map(ProductivityComparisonReportDTO::getAverageIntValueTotal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO)
                .divide(BigDecimal.valueOf(total), 2 , HALF_UP);

        parameters.put("total", total);
        parameters.put("spentTotal", spentTotal);
        parameters.put("spentAverage", spentAverage);
        parameters.put("repairTimeTotal", repairTimeTotal);
        parameters.put("repairTimeTotalAvg", repairTimeTotalAvg);
        parameters.put("repairValueTotal", repairValueTotal);
        parameters.put("repairValueTotalAverage", repairValueTotalAverage);
        parameters.put("averageExtTimeTotal", averageExtTimeTotal);
        parameters.put("averageExtValueTotal", averageExtValueTotal);
        parameters.put("averageIntValueTotal", averageIntValueTotal);

        return parameters;
    }

    private List<ProductivityComparisonReportDTO> groupProductivityComparisonReport(List<ProductivityComparisonDTO> productivityComparisonDTO){
        List<ProductivityComparisonReportDTO> reportData = new ArrayList<>();
        LinkedHashMap<String, List<ProductivityComparisonDTO>> mapValues = productivityComparisonDTO.parallelStream()
                .collect(groupingBy(ProductivityComparisonDTO::getTechnicalStaffCod, LinkedHashMap::new, Collectors.toList()));
        mapValues.forEach((key, value) -> {
            ProductivityComparisonReportDTO reportDTO = ProductivityComparisonReportDTO.builder()
                    .items(value)
                    .technicalStaffCod(key)
                    .technicalStaffName(value.get(0).getTechnicalStaffName())
                    .build();
            reportData.add(reportDTO);
        });
        return reportData;
    }
}
