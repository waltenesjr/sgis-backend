package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.AnalyticRepairDTO;
import br.com.oi.sgis.dto.AnalyticRepairReportDTO;
import br.com.oi.sgis.enums.BreaksAnalyticRepairEnum;
import br.com.oi.sgis.repository.RepairTicketRepository;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RepairTicketAnalysisService {

    private final ReportService reportService;
    private final RepairTicketRepository repairTicketRepository;

    public byte[] report(List<AnalyticRepairDTO> analyticRepairDTOS, BreaksAnalyticRepairEnum breakTotals) throws JRException, IOException {
        Map<String, List<AnalyticRepairDTO>>  map = mapValues(breakTotals, analyticRepairDTOS);
        List<AnalyticRepairReportDTO> reportData = new ArrayList<>();
        map.forEach((key, value) -> {
            value.parallelStream().forEach(q -> q.setRepairExternalIn(repairTicketRepository.externalInternalRepair(q.getBrNumber())));
            BigDecimal valueTotal = value.stream().map(AnalyticRepairDTO::getValue).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            reportData.add(AnalyticRepairReportDTO.builder().items(value).groupBy(key)
                    .total(value.size())
                    .valueTotal(valueTotal)
                    .build());
        });
        return reportService.analysisRepairReport(reportData);
    }
    private Map<String, List<AnalyticRepairDTO>> mapValues(BreaksAnalyticRepairEnum breakTotals, List<AnalyticRepairDTO> itens) {
        Map<String, List<AnalyticRepairDTO>> map = new HashMap<>();
        if(breakTotals == null){
            map.put("", itens);
        }else{
            map = getGroupByList(itens, breakTotals);
        }
        return map;
    }

    private Map<String, List<AnalyticRepairDTO>> getGroupByList(List<AnalyticRepairDTO> itens, BreaksAnalyticRepairEnum breakResults) {
        switch (breakResults){
            case MODEL:return groupByModel(itens);
            case SITUATION: return groupBySituation(itens);
            case DEFECT:return groupByDefect(itens);
            case RC: return groupByRepairCenter(itens);
            default:return groupByOrigin(itens);
        }
    }

    private Map<String, List<AnalyticRepairDTO>> groupByRepairCenter(List<AnalyticRepairDTO> itens) {
        return itens.parallelStream()
                .sorted(Comparator.comparing(AnalyticRepairDTO::getRepairCenter,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(groupingBy(m-> getString(m.getRepairCenter()), LinkedHashMap::new,
                        Collectors.toList()));
    }

    private Map<String, List<AnalyticRepairDTO>> groupByDefect(List<AnalyticRepairDTO> itens) {
        return itens.parallelStream()
                .sorted(Comparator.comparing(AnalyticRepairDTO::getDefect,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(groupingBy(m-> getString(m.getDefect()), LinkedHashMap::new,
                        Collectors.toList()));
    }

    private Map<String, List<AnalyticRepairDTO>> groupByModel(List<AnalyticRepairDTO> itens) {
        return itens.parallelStream()
                .sorted(Comparator.comparing(AnalyticRepairDTO::getModelEquipment,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(groupingBy(m-> getString(m.getModelEquipment()), LinkedHashMap::new,
                        Collectors.toList()));
    }
    private Map<String, List<AnalyticRepairDTO>> groupBySituation(List<AnalyticRepairDTO> itens) {
        return itens.parallelStream()
                .sorted(Comparator.comparing(AnalyticRepairDTO::getSituation,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(groupingBy(m-> getString(m.getSituation()), LinkedHashMap::new,
                        Collectors.toList()));
    }

    private Map<String, List<AnalyticRepairDTO>> groupByOrigin(List<AnalyticRepairDTO> itens) {
        return itens.parallelStream()
                .sorted(Comparator.comparing(AnalyticRepairDTO::getOrigin,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(groupingBy(m-> getString(m.getOrigin()), LinkedHashMap::new,
                        Collectors.toList()));
    }

    private String getString(String string) {
        return string != null ? string : "";
    }
}
