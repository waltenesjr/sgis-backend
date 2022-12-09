package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.Estimate;
import br.com.oi.sgis.entity.ItemEstimate;
import br.com.oi.sgis.entity.Level;
import br.com.oi.sgis.entity.TicketIntervention;
import br.com.oi.sgis.exception.EstimateNotFoundException;
import br.com.oi.sgis.mapper.EstimateMapper;
import br.com.oi.sgis.repository.EstimateRepository;
import br.com.oi.sgis.repository.ItemEstimateRepository;
import br.com.oi.sgis.repository.TicketInterventionRepository;
import br.com.oi.sgis.service.factory.EstimateFactory;
import br.com.oi.sgis.service.validator.Validator;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EstimateService {

    private final EstimateRepository estimateRepository;
    private final ItemEstimateRepository itemEstimateRepository;
    private final TicketInterventionRepository ticketInterventionRepository;
    private final ReportService reportService;
    private static final EstimateMapper estimateMapper = EstimateMapper.INSTANCE;
    private final EstimateFactory estimateFactory;
    private final Validator<EstimateDTO> validator;
    private final NasphService nasphService;

    public PaginateResponseDTO<EstimateDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            term="";

        return PageableUtil.paginate(estimateRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(estimateMapper::toDTO));
    }

    public PaginateResponseDTO<EstimateDTO> listAllOpenPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            term="";

        return PageableUtil.paginate(estimateRepository.findLikeOpen(term.toUpperCase(Locale.ROOT).trim(), paging).map(estimateMapper::toDTO));
    }

    public PaginateResponseDTO<EstimateDTO> listAllSentPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            term="";

        return PageableUtil.paginate(estimateRepository.findLikeSent(term.toUpperCase(Locale.ROOT).trim(), paging).map(estimateMapper::toDTO));
    }

    public EstimateDTO findById(String id) throws EstimateNotFoundException {
        Estimate estimate = verifyIfExists(id);
        return estimateMapper.toDTO(estimate);
    }

    private Estimate verifyIfExists(String id) throws EstimateNotFoundException {
        return estimateRepository.findById(id)
                .orElseThrow(()-> new EstimateNotFoundException(MessageUtils.ESTIMATE_NOT_FOUND_BY_ID.getDescription() + id));
    }
    @Transactional(rollbackFor = IllegalArgumentException.class)
    public MessageResponseDTO createEstimate(EstimateDTO estimateDTO) {
        Estimate estimateToSave = estimateFactory.createEstimate(estimateDTO);
        return createMessageResponse(estimateToSave.getId(),MessageUtils.ESTIMATE_SAVE_SUCCESS.getDescription(),
                HttpStatus.CREATED);
    }
    @Transactional(rollbackFor = IllegalArgumentException.class)
    public MessageResponseDTO updateEstimate(EstimateDTO estimateDTO) throws EstimateNotFoundException {
        Estimate estimate = verifyIfExists(estimateDTO.getId());
        validator.validate(estimateDTO);
        if(estimate.getSendDate() != null)
            throw new IllegalArgumentException(MessageUtils.ESTIMATE_EXTERNAL_REP_SEND_ERROR.getDescription());
        return update(estimateDTO);
    }

    private MessageResponseDTO update(EstimateDTO estimateDTO) {
        Estimate estimateToSave = estimateMapper.toModel(estimateDTO);
        List<ItemEstimate> itemEstimateUpdate = estimateToSave.getItemEstimates();
        estimateToSave.setItemEstimates(null);
        updateItemEstimate(estimateToSave, itemEstimateUpdate);
        estimateRepository.save(estimateToSave);
        return createMessageResponse(estimateToSave.getId(), MessageUtils.ESTIMATE_UPDATE_SUCCESS.getDescription(),
                HttpStatus.OK);
    }

    private void updateItemEstimate(Estimate estimate, List<ItemEstimate> itemEstimates) {
        List<ItemEstimate> items = itemEstimates;
        List<ItemEstimate> oldItems = itemEstimateRepository.findAllByIdEstimateId(estimate.getId());
        if(items.isEmpty() && oldItems.isEmpty())
            return;
        if(oldItems.isEmpty()) {
            estimateFactory.createItemEstimate(estimate, items);
        }
        else if(items.isEmpty()){
            removeItemEstimate(oldItems);
        }else {
            List<ItemEstimate> removed = new ArrayList<>();
            oldItems.forEach(o -> {
                AtomicInteger count = new AtomicInteger();
                items.forEach(ie ->{
                    if(ie.getId().getTicketIntervention().getId().getSequence().equals(o.getId().getTicketIntervention().getId().getSequence())){
                        count.getAndIncrement();
                    }
                });
                if(count.get() == 0)
                    removed.add(o);
            });
            removeItemEstimate(removed);
            List<ItemEstimate> newItems = items.stream().filter(ie-> ie.getId().getTicketIntervention().getId().getSequence() == null).collect(Collectors.toList());
            List<ItemEstimate> itemsToUpdate = items.stream().filter(ie-> ie.getId().getTicketIntervention().getId().getSequence() != null).collect(Collectors.toList());
            estimateFactory.createItemEstimate(estimate, newItems);
            updateItemEstimate(itemsToUpdate);
        }
    }

    private void updateItemEstimate(List<ItemEstimate> itemsToUpdate) {
        if(itemsToUpdate == null || itemsToUpdate.isEmpty())
            return;
        itemsToUpdate.forEach(ie -> {
            TicketIntervention ti =  ie.getId().getTicketIntervention();
            ticketInterventionRepository.update(ti.getId().getSequence(),ti.getId().getRepairTicket().getId(),
                    ti.getIntervention().getId(), ti.getOperator().getId(),ti.getObservation() );
            itemEstimateRepository.update(ie.getValue(), ie.getProvider(), ti.getId().getSequence(), ti.getId().getRepairTicket().getId(),
                    ie.getId().getEstimate().getId());

        });

    }

    private void removeItemEstimate(List<ItemEstimate> removed) {
        if(removed == null || removed.isEmpty())
            return;
        List<TicketIntervention> removedInterventions = removed.stream().map(ie-> ie.getId().getTicketIntervention()).collect(Collectors.toList());
        itemEstimateRepository.deleteAll(removed);
        ticketInterventionRepository.deleteAll(removedInterventions);
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).title("Sucesso!").build();
    }

    public byte[] estimateReport(String search, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<EstimateDTO> estimateDTOS  =  listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, search).getData();
        return getReport(estimateDTOS);
    }

    private byte[] getReport(List<EstimateDTO> estimateDTOS) throws JRException, IOException {
        if (estimateDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        List<EstimateReportDTO> estimateReportDTOS = new ArrayList<>();
        estimateDTOS.stream().forEach(e -> {
            List<EstimateItemReportDTO> items = itemEstimateRepository.estimateItemReport(e.getId());
            BigDecimal totalWeight = items.stream().map(EstimateItemReportDTO::getWeight).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            BigDecimal totalValue = items.stream().map(EstimateItemReportDTO::getValue).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            EstimateReportDTO estimateReportDTO = EstimateReportDTO.builder()
                    .id(e.getId()).company(e.getCompany().getId()).companyDescription(e.getCompany().getCompanyName())
                    .contract(e.getContract() != null ? e.getContract().getId() : "").date(e.getDate()).sendDate(e.getSendDate()).fiscalNote(e.getFiscalNote())
                    .warrantyDate(e.getWarrantyDate()).observation(e.getObservation()).phone(e.getPhone()).address(e.getAddress())
                    .deliveryDays(e.getDeliveryDays()).warrantyDays(e.getWarrantyDays()).department(e.getDepartment().getId())
                    .contact(e.getContact()).items(items).totalWeight(totalWeight).totalValue(totalValue).build();

            estimateReportDTOS.add(estimateReportDTO);
        });
        Map<String, Object> parameters = new HashMap<>();
        return reportService.estimateReport(estimateReportDTOS, parameters);
    }

    public byte[] estimateSentReport(String search, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<EstimateDTO> estimateDTOS  =  listAllSentPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, search).getData();
        return getReport(estimateDTOS);
    }

    public MessageResponseDTO externalOutput(EstimateExternalOutputDTO estimateExternalOutputDTO) throws EstimateNotFoundException {
        Estimate estimate = verifyIfExists(estimateExternalOutputDTO.getEstimateId());
        validateExternalOutput(estimateExternalOutputDTO, estimate);
        return nasphService.estimateExternalOutput(estimateExternalOutputDTO);
    }

    private void validateExternalOutput(EstimateExternalOutputDTO estimateExternalOutputDTO, Estimate estimate) {
        if(estimateExternalOutputDTO.getFiscalNoteDate().isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException(MessageUtils.ESTIMATE_EXTERNAL_REP_DATE_NF_ERROR.getDescription());
        if(estimate.getSendDate() != null)
            throw new IllegalArgumentException(MessageUtils.ESTIMATE_EXTERNAL_REP_SEND_OUT_ERROR.getDescription());
        if(!estimate.getDepartment().getId().equals(Utils.getUser().getDepartmentCode().getId())){
            throw new IllegalArgumentException(MessageUtils.ESTIMATE_EXTERNAL_REP_DEPARTMENT_ERROR.getDescription());
        }
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    public MessageResponseDTO updateSentEstimate(EstimateDTO estimateDTO) throws EstimateNotFoundException {
        Estimate estimate = verifyIfExists(estimateDTO.getId());
        validateSentEstimate(estimateDTO, estimate);
        return update(estimateDTO);
    }

    private void validateSentEstimate(EstimateDTO estimateDTO, Estimate estimate) {
        if(estimateDTO.getFiscalNoteDate().isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException(MessageUtils.ESTIMATE_EXTERNAL_REP_DATE_NF_ERROR.getDescription());
        if(estimate.getSendDate() == null)
            throw new IllegalArgumentException(MessageUtils.ESTIMATE_NOT_SENT_ERROR.getDescription());
        List<Integer> userLevels = Utils.getUser().getLevels().stream().map(Level::getLvl).collect(Collectors.toList());
        if(!estimateDTO.getDepartment().getId().equals(Utils.getUser().getDepartmentCode().getId())
                && !userLevels.contains(0))
            throw new IllegalArgumentException(MessageUtils.ESTIMATE_SENT_DEPARTMENT_ERROR.getDescription());
    }

    public MessageResponseDTO cancelEstimate(String estimateID) throws EstimateNotFoundException {
        verifyIfExists(estimateID);
        return nasphService.cancelEstimate(estimateID);
    }

    public MessageResponseDTO returnRepair(RepairExternalReturnDTO repairExternalReturnDTO) {
        validateReturnRepair(repairExternalReturnDTO);
        return nasphService.returnExternalRepair(repairExternalReturnDTO);
    }

    private void validateReturnRepair(RepairExternalReturnDTO repairExternalReturnDTO) {
        if(repairExternalReturnDTO.getArrivalDate().isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException(MessageUtils.EXTERNAL_REPAIR_RETURN_ARRIVAL_ERROR.getDescription());
        if(repairExternalReturnDTO.getFiscalNoteDate().isAfter(repairExternalReturnDTO.getArrivalDate()))
            throw new IllegalArgumentException(MessageUtils.EXTERNAL_REPAIR_RETURN_NF_DATE_ERROR.getDescription());
        if(Boolean.TRUE.equals(repairExternalReturnDTO.getSubstitution())&& repairExternalReturnDTO.getNewBarcode()==null)
            throw new IllegalArgumentException(MessageUtils.EXTERNAL_REPAIR_RETURN_SUBSTITUTION_ERROR.getDescription());
    }
}
