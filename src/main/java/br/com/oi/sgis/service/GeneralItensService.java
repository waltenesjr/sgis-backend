package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.GeneralItensCriteriaDTO;
import br.com.oi.sgis.dto.GeneralItensDTO;
import br.com.oi.sgis.dto.GeneralItensReportDTO;
import br.com.oi.sgis.enums.GeneralItensReportBreakEnum;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.ReportUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class GeneralItensService {

    private final UnityRepository generalItensRepository;
    private final ReportService reportService;


    private List<GeneralItensReportDTO> listItens(GeneralItensCriteriaDTO criteriaDTO){
        if(criteriaDTO.getSituationInitialDate() != null && criteriaDTO.getSituationFinalDate()!=null){
            Assert.isTrue(criteriaDTO.getSituationFinalDate().isAfter(criteriaDTO.getSituationInitialDate()), MessageUtils.INVALID_PERIOD.getDescription());
        }
        List<GeneralItensDTO> itens = generalItensRepository.listGeneralItens(criteriaDTO);
        if(itens.isEmpty()){
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        }
        Map<String, List<GeneralItensDTO>> map = mapValues(itens, criteriaDTO.getOrder());

        List<GeneralItensReportDTO> reportData = new ArrayList<>();
        map.forEach((key, value) -> {
            Long total = (long) value.size();
            reportData.add(GeneralItensReportDTO.builder()
                            .totalItens(total).generalItens(value)
                    .groupBy(criteriaDTO.getOrder() != null ? key : null)
                    .typeGroup(criteriaDTO.getOrder() != null ? criteriaDTO.getOrder().getOrderBy() : null).build());
        });
        return reportData;
    }

    @SneakyThrows
    public byte[] report(GeneralItensCriteriaDTO generalItensCriteriaDTO, TypeDocEnum typeDocEnum)  {

        List<GeneralItensReportDTO> generalItensReportDTOS = this.listItens(generalItensCriteriaDTO);
        String file = "/reports/generalItensReportExcel.jrxml";
        if(typeDocEnum.getCod().equals(TypeDocEnum.PDF.getCod()))
            file = "/reports/generalItensReport.jrxml";
        JasperReport jasperReport = ReportUtils.getJasperReport(file);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("totalGeral", generalItensReportDTOS.stream().mapToLong(GeneralItensReportDTO::getTotalItens).sum());
        if(generalItensCriteriaDTO.getSituationInitialDate() !=null)
            parameters.put("periodoSituacao", generalItensCriteriaDTO.getSituationPeriod());

       return   reportService.fillGeneralItensReport(generalItensReportDTOS, jasperReport, parameters, typeDocEnum);

    }


    private Map<String, List<GeneralItensDTO>>mapValues(List<GeneralItensDTO> itens, GeneralItensReportBreakEnum order) {
        Map<String, List<GeneralItensDTO>> map = new HashMap<>();
        if(order == null){
            map.put("", itens);
        }else{
            map = getGroupByList(itens, order);
        }
        return map;

    }

    private Map<String, List<GeneralItensDTO>> getGroupByList(List<GeneralItensDTO> itens, GeneralItensReportBreakEnum order) {
        switch (order){
            case ESTACAO:return groupByStation(itens);
            case UNIDADE:return groupByUnity(itens);
            case EQUIPAMENTO: return groupByEquipament(itens);
            case TECNICO: return groupByTechnician(itens);
            case SITUACAO: return groupBySituation(itens);
            case FABRICANTE:return groupByManufacturer(itens);
            case DEPOSITORIO:return groupByDeposit(itens);
            default: return groupByResponsible(itens);
        }
    }

    private Map<String, List<GeneralItensDTO>> groupByResponsible(List<GeneralItensDTO> itens) {
        return itens.parallelStream()
                .sorted(Comparator.comparing(GeneralItensDTO::getResponsibleId,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(groupingBy(m-> getString(m.getResponsibleId()), LinkedHashMap::new,
                        Collectors.toList()));
    }

    private Map<String, List<GeneralItensDTO>> groupByDeposit(List<GeneralItensDTO> itens) {
        return itens.parallelStream()
                .sorted(Comparator.comparing(GeneralItensDTO::getDepositId,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(groupingBy(m-> getString(m.getDepositId()), LinkedHashMap::new,
                        Collectors.toList()));
    }

    private Map<String, List<GeneralItensDTO>> groupByManufacturer(List<GeneralItensDTO> itens) {
        return itens.parallelStream()
                .sorted(Comparator.comparing(GeneralItensDTO::getCompanyName,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(groupingBy(m-> getString(m.getCompanyName()), LinkedHashMap::new,
                        Collectors.toList()));
    }

    private Map<String, List<GeneralItensDTO>> groupBySituation(List<GeneralItensDTO> itens) {
        return itens.parallelStream()
                .sorted(Comparator.comparing(GeneralItensDTO::getSituationId,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(groupingBy(m-> getString(m.getSituationId()), LinkedHashMap::new,
                        Collectors.toList()));
    }

    private Map<String, List<GeneralItensDTO>> groupByTechnician(List<GeneralItensDTO> itens) {
        return itens.parallelStream()
                .sorted(Comparator.comparing(GeneralItensDTO::getTechnicianId,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(groupingBy(m-> getString(m.getTechnicianId()), LinkedHashMap::new,
                        Collectors.toList()));
    }

    private Map<String, List<GeneralItensDTO>> groupByEquipament(List<GeneralItensDTO> itens) {
        return itens.parallelStream()
                .sorted(Comparator.comparing(GeneralItensDTO::getModelId,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(groupingBy(m-> getString(m.getModelId()), LinkedHashMap::new,
                        Collectors.toList()));
    }

    private Map<String, List<GeneralItensDTO>> groupByUnity(List<GeneralItensDTO> itens) {
        return itens.parallelStream()
                .sorted(Comparator.comparing(GeneralItensDTO::getUnityCodeId,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(groupingBy(m-> getString(m.getUnityCodeId()), LinkedHashMap::new,
                        Collectors.toList()));
    }

    private Map<String, List<GeneralItensDTO>> groupByStation(List<GeneralItensDTO> itens) {
        return itens.parallelStream()
                .sorted(Comparator.comparing(GeneralItensDTO::getStationId,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(groupingBy(m-> getString(m.getStationId()), LinkedHashMap::new,
                        Collectors.toList()));
    }

    private String getString(String string) {
        return string != null ? string : "";
    }

}
