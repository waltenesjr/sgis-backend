package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.SummaryItemCriteriaReportDTO;
import br.com.oi.sgis.dto.SummaryItemViewDTO;
import br.com.oi.sgis.dto.SummaryItemViewReportDTO;
import br.com.oi.sgis.enums.ItensSumaryReportBreakEnum;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.ReportUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SummaryItemService {
    private final UnityRepository summaryItemRepository;
    private final ReportService reportService;

    private final Validator<SummaryItemCriteriaReportDTO> summaryItemReportDTOValidator;

    private List<SummaryItemViewReportDTO> listItens(SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO){
        summaryItemReportDTOValidator.validate(summaryItemCriteriaReportDTO);

        List<SummaryItemViewDTO> itens =  summaryItemRepository.findBySummaryParams(summaryItemCriteriaReportDTO);
        if(itens.isEmpty()){
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        }
        Map<String, List<SummaryItemViewDTO>> map = mapValues(summaryItemCriteriaReportDTO, itens);
        List<SummaryItemViewReportDTO> viewReport = new ArrayList<>();
        map.forEach((key, value) -> {
            Long total = value.stream().map(SummaryItemViewDTO::getTotal).reduce(0L, Long::sum);
            viewReport.add(SummaryItemViewReportDTO.builder().groupItens(value)
                    .totalItens(total)
                    .typeGroup(summaryItemCriteriaReportDTO.getBreakResults() != null ? summaryItemCriteriaReportDTO.getBreakResults().getOrderBy() : null)
                    .groupBy(summaryItemCriteriaReportDTO.getBreakResults() != null ? key : null)
                    .build());
        });
        return viewReport;
    }

    @SneakyThrows
    public byte[] report(SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO, TypeDocEnum typeDocEnum )  {
        List<SummaryItemViewReportDTO> itens = listItens(summaryItemCriteriaReportDTO);
        String file = "/reports/summaryItemReportExcel.jrxml";
        if(typeDocEnum.getCod().equals(TypeDocEnum.PDF.getCod()))
            file = "/reports/summaryItemReport.jrxml";
        JasperReport jasperReport = ReportUtils.getJasperReport(file);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("totalGeral", itens.stream().mapToLong(SummaryItemViewReportDTO::getTotalItens).sum());
        return reportService.fillSummaryItensReport(itens, jasperReport, parameters, typeDocEnum);

    }


    private Map<String, List<SummaryItemViewDTO>> mapValues(SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO, List<SummaryItemViewDTO> itens) {
        Map<String, List<SummaryItemViewDTO>> map = new HashMap<>();
        if(summaryItemCriteriaReportDTO.getBreakResults() == null){
            map.put("", itens);
        }else{
            map = getGroupByList(itens, summaryItemCriteriaReportDTO.getBreakResults());
        }
        return map;
    }

    private Map<String, List<SummaryItemViewDTO>> getGroupByList(List<SummaryItemViewDTO> itens, ItensSumaryReportBreakEnum breakResults) {
        switch (breakResults){
            case EQUIPAMENTO:return groupByModel(itens);
            case UNIDADE: return groupByUnity(itens);
            default:return groupByStation(itens);
        }
    }

    private Map<String, List<SummaryItemViewDTO>> groupByModel(List<SummaryItemViewDTO> itens) {
        return itens.parallelStream()
                .sorted(Comparator.comparing(SummaryItemViewDTO::getModelCode,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(groupingBy(m-> getString(m.getModelCode()), LinkedHashMap::new,
                        Collectors.toList()));
    }
    private Map<String, List<SummaryItemViewDTO>> groupByUnity(List<SummaryItemViewDTO> itens) {
        return itens.parallelStream()
                .sorted(Comparator.comparing(SummaryItemViewDTO::getUnityCode,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(groupingBy(m-> getString(m.getUnityCode()), LinkedHashMap::new,
                        Collectors.toList()));
    }

    private Map<String, List<SummaryItemViewDTO>> groupByStation(List<SummaryItemViewDTO> itens) {
        return itens.parallelStream()
                .sorted(Comparator.comparing(SummaryItemViewDTO::getStationCode,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(groupingBy(m-> getString(m.getStationCode()), LinkedHashMap::new,
                        Collectors.toList()));
    }

    private String getString(String string) {
        return string != null ? string : "";
    }





}
