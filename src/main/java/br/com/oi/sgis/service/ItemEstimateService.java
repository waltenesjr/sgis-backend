package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.Department;
import br.com.oi.sgis.entity.Estimate;
import br.com.oi.sgis.entity.ItemEstimate;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.exception.ItemEstimateNotFoundException;
import br.com.oi.sgis.mapper.ItemEstimateMapper;
import br.com.oi.sgis.repository.DepartmentRepository;
import br.com.oi.sgis.repository.EstimateRepository;
import br.com.oi.sgis.repository.ItemEstimateRepository;
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
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ItemEstimateService {
    private final ItemEstimateRepository itemEstimateRepository;
    private final EstimateRepository estimateRepository;
    private final ReportService reportService;
    private final DepartmentRepository departmentRepository;
    private static final ItemEstimateMapper itemEstimateMapper = ItemEstimateMapper.INSTANCE;
    private final NasphService nasphService;

    public PaginateResponseDTO<ItemEstimateDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, ItemEstimatesAnalysisDTO dto) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (dto.getInitialDate()!=null)
            Utils.isPeriodInvalid(dto.getInitialDate(), dto.getFinalDate());
        String departmentId = Utils.getUser().getDepartmentCode().getId();
        return PageableUtil.paginate(itemEstimateRepository.findLike( dto, departmentId, paging).map(itemEstimateMapper::toDTO));
    }

    public ItemEstimateDTO findById(ItemEstimateDTO dto) throws ItemEstimateNotFoundException {
        ItemEstimate itemEstimate = verifyIfExists(dto.getEstimate(), dto.getTicketIntervention().getRepairTicket().getBrNumber(), dto.getTicketIntervention().getSequence());
        return itemEstimateMapper.toDTO(itemEstimate);
    }

    private ItemEstimate verifyIfExists(String estimateId, String brNumber, Long sequence) throws ItemEstimateNotFoundException{
        return itemEstimateRepository.findById( brNumber, sequence, estimateId)
                .orElseThrow(()-> new ItemEstimateNotFoundException(MessageUtils.ITEM_ESTIMATE_NOT_FOUND_BY_ID.getDescription()));
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).title("Sucesso!").build();
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    public MessageResponseDTO updateAnalysis(List<ItemEstimateDTO> items) throws ItemEstimateNotFoundException {
        try { for (ItemEstimateDTO item : items) {
            findById(item);
            itemEstimateRepository.updateAnalysis(item.getEstimate(), item.getTicketIntervention().getRepairTicket().getBrNumber(),
                    item.getTicketIntervention().getSequence(), item.isAnalyzed());
        }
            return createMessageResponse("", MessageUtils.ITEM_ESTIMATE_ANALYSIS_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ITEM_ESTIMATE_ANALYSIS_UPDATE_ERROR.getDescription());
        }
    }

    public MessageResponseDTO updateCancelApprove(List<ApproveCancelItemEstimateDTO> items) throws ItemEstimateNotFoundException {
        try { for (ApproveCancelItemEstimateDTO item : items) {
            ItemEstimate itemEstimate = verifyIfExists(item.getEstimateID(), item.getBrNumber(), item.getSequence());
            Unity unityItem = itemEstimate.getId().getTicketIntervention().getUnity();
            if(Boolean.TRUE.equals(item.getApprove()))
                nasphService.approveItemEstimate(unityItem.getId(),
                        itemEstimate.getProvider(), itemEstimate.getValue());
            else {
                nasphService.cancelItemEstimate(unityItem.getId());
            }
        }
            return createMessageResponse("", MessageUtils.ITEM_ESTIMATE_APPROVE_CANCEL_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ITEM_ESTIMATE_CANCEL_CANCEL_ERROR.getDescription());
        }
    }

    public byte[] analysisReport(ItemEstimatesAnalysisDTO itemDTO, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<ItemEstimateDTO> itemEstimateDTOS  =  listAllPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, itemDTO).getData();
        if(itemEstimateDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = getParameterAnalysisReport();
        List<ItemEstimatesAnalysisReportDTO> reportDTOList = new ArrayList<>();
        List<ItemEstimatesAnalysisReportDTO.ItemsAnalysis> items = getItemsAnalyses(itemEstimateDTOS);
        getItemsAnalysesReportData(reportDTOList, items);
        return reportService.analysisItemEstimateReport(reportDTOList, parameters);
    }

    private void getItemsAnalysesReportData(List<ItemEstimatesAnalysisReportDTO> reportDTOList, List<ItemEstimatesAnalysisReportDTO.ItemsAnalysis> items) {
        Map<String, List<ItemEstimatesAnalysisReportDTO.ItemsAnalysis>> mapValues =  items.parallelStream().collect(groupingBy(ItemEstimatesAnalysisReportDTO.ItemsAnalysis::getCompany, LinkedHashMap::new,
                Collectors.toList()));
        mapValues.forEach((key, value) -> {
            Integer totalValue = value.stream().map(ItemEstimatesAnalysisReportDTO.ItemsAnalysis::getRepairValue).reduce(BigDecimal::add).orElse(BigDecimal.ZERO).intValue();
            ItemEstimatesAnalysisReportDTO itemEstimatesAnalysisDTO = ItemEstimatesAnalysisReportDTO.builder()
                    .itemsAnalyses(value).company(key).companyName(value.get(0).getCompanyName())
                    .totalItens(value.size()).totalToRepair(0).totalValue(totalValue)
                    .build();
            reportDTOList.add(itemEstimatesAnalysisDTO);
        });
    }

    private List<ItemEstimatesAnalysisReportDTO.ItemsAnalysis> getItemsAnalyses(List<ItemEstimateDTO> itemEstimateDTOS) {
        List<ItemEstimatesAnalysisReportDTO.ItemsAnalysis> items = new ArrayList<>();
        itemEstimateDTOS.forEach(i -> {
            Estimate estimate = estimateRepository.findById(i.getEstimate()).orElse(null);
            String description = i.getTicketIntervention().getUnity().getUnityCode().getId() + " - " +
                    i.getTicketIntervention().getUnity().getUnityCode().getDescription() ;
            assert estimate != null;
            ItemEstimatesAnalysisReportDTO.ItemsAnalysis item = ItemEstimatesAnalysisReportDTO.ItemsAnalysis.builder().analyzed(i.isAnalyzed()).estimate(i.getEstimate()).serieNumber(i.getTicketIntervention().getUnity().getSerieNumber())
                    .description(description).barcode(i.getTicketIntervention().getUnity().getId())
                    .brNumber(i.getTicketIntervention().getRepairTicket().getBrNumber()).date(i.getTicketIntervention().getRepairTicket().getOpenDate())
                    .fiscalNote(estimate.getFiscalNote()).fiscalNoteDate(estimate.getFiscalNoteDate())
                    .provider(i.getProvider()).department(i.getTicketIntervention().getRepairTicket().getDevolutionDepartment())
                    .company(estimate.getCompany().getId()).companyName(estimate.getCompany().getCompanyName()).value(i.getTicketIntervention().getUnity().getUnityCode().getValue()).repairValue(i.getValue())
                    .build();
            items.add(item);
        });
        return items;
    }

    private Map<String, Object> getParameterAnalysisReport() {
        Map<String, Object> parameters = new HashMap<>();
        Department departmentUser = departmentRepository.findById(Utils.getUser().getDepartmentCode().getId()).orElse(null);
        assert departmentUser != null;
        parameters.put("nameManager", departmentUser.getManager() != null ? departmentUser.getManager().getTechnicianName() : "");
        parameters.put("departmentManager", departmentUser.getId());
        parameters.put("departmentName", departmentUser.getDescription());
        parameters.put("phone", departmentUser.getPhone());
        return parameters;
    }
}
