package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.repository.RepairTicketRepository;
import br.com.oi.sgis.util.MessageUtils;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CostComparisonService {
    private final ReportService reportService;
    private final RepairTicketRepository repairTicketRepository;

    public byte[] report(CostComparisonRepairFilterDTO filterDto) throws JRException, IOException {
        List<CostComparisonRepDTO> costComparisonRepDTOS = repairTicketRepository.findCostRepair(filterDto.getRepairCenter(), filterDto.getInitialDate(), filterDto.getFinalDate());
        if (costComparisonRepDTOS.isEmpty()) {
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        }
        try {
            List<CostComparisonRepReportDTO> equipTypeDTOS = mapByEquipment(costComparisonRepDTOS);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("repairCenter", filterDto.getRepairCenter());
            parameters.put("reportType", filterDto.getDetail().getOrderBy());
            return reportService.costComparisonReport(equipTypeDTOS, parameters);
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

    private List<CostComparisonRepReportDTO> mapByEquipment(List<CostComparisonRepDTO> costComparisonRepDTOS) {
        List<CostComparisonRepEquipDTO> equipmentData = new ArrayList<>();
        LinkedHashMap<String, List<CostComparisonRepDTO>> mapValues =  costComparisonRepDTOS.parallelStream()
                .collect(groupingBy(CostComparisonRepDTO::getEquipmentModel, LinkedHashMap::new,
                        Collectors.toList()));
        mapValues.forEach((key, value) -> {
            CostComparisonRepEquipDTO equipGroup = CostComparisonRepEquipDTO.builder()
                    .unities(value)
                    .equipment(key)
                    .equipmentDescription(value.get(0).getEquipmentModelDescription())
                    .equipmentType(value.get(0).getEquipmentType()).equipmentTypeDescription(value.get(0).getEquipmentTypeDescription())
                    .build();
            equipmentData.add(equipGroup);
        });
        return mapByEquipmentType(equipmentData);
    }

    private List<CostComparisonRepReportDTO> mapByEquipmentType(List<CostComparisonRepEquipDTO> costComparisonRepEquipDTOS) {
        List<CostComparisonRepReportDTO> reportData = new ArrayList<>();
        LinkedHashMap<String, List<CostComparisonRepEquipDTO>> mapValues =  costComparisonRepEquipDTOS.parallelStream()
                .collect(groupingBy(CostComparisonRepEquipDTO::getEquipmentType, LinkedHashMap::new,
                        Collectors.toList()));
        mapValues.forEach((key, value) -> {
            CostComparisonRepReportDTO equipTypGroup = CostComparisonRepReportDTO.builder()
                    .listEquipments(value)
                    .equipmentType(key)
                    .description(value.get(0).getEquipmentTypeDescription())
                    .build();
            reportData.add(equipTypGroup);
        });
        return reportData;
    }
}
