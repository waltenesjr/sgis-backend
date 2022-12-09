package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.ItensInstallByStealReasonCriteriaDTO;
import br.com.oi.sgis.dto.ItensInstallByStealReasonDTO;
import br.com.oi.sgis.dto.ItensInstallByStealReasonReportDTO;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.ReportUtils;
import br.com.oi.sgis.util.Utils;
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
public class ItensInstallByStealReasonService {

    private final UnityRepository itensByStealReasonRepository;
    private final DepartmentService departmentService;
    private final ReportService reportService;


    public List<ItensInstallByStealReasonReportDTO> listItens(ItensInstallByStealReasonCriteriaDTO criteriaDTO) throws DepartmentNotFoundException {
        criteriaValidation(criteriaDTO);

        List<ItensInstallByStealReasonDTO> itens =   itensByStealReasonRepository.findByParamsSteal(criteriaDTO);
        if (itens.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, List<ItensInstallByStealReasonDTO>> groupByReason = groupedByReason(itens);

        List<ItensInstallByStealReasonReportDTO> reportData = new ArrayList<>();
        groupByReason.forEach((key, value) -> reportData.add(ItensInstallByStealReasonReportDTO.builder().reasonInstallation(key).itens(value).build()));
        return reportData;
    }

    @SneakyThrows
    public byte[] report(ItensInstallByStealReasonCriteriaDTO criteriaDTO, TypeDocEnum type) {
        List<ItensInstallByStealReasonReportDTO> itensInstallByStealReasonReport = this.listItens(criteriaDTO);
        String file = "/reports/itensByStealReasonReportExcel.jrxml";
        if(type.getCod().equals(TypeDocEnum.PDF.getCod()))
            file = "/reports/itensByStealReasonReport.jrxml";
        JasperReport jasperReport = ReportUtils.getJasperReport(file);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("lotacao", Utils.getUser().getDepartmentCode().getId());
        parameters.put("user", Utils.getUser().getId());
        parameters.put("periodo", criteriaDTO.getPeriod());
        return reportService.fillItensBySteal(itensInstallByStealReasonReport, jasperReport, parameters, type);

    }

    private  Map<String, List<ItensInstallByStealReasonDTO>>groupedByReason(List<ItensInstallByStealReasonDTO> itens) {

        Map<String, List<ItensInstallByStealReasonDTO>> map;
        map = itens.stream()
                .collect(groupingBy(ItensInstallByStealReasonDTO::getReason, LinkedHashMap::new,
                        Collectors.toList()));

        return map;

    }

    private void criteriaValidation(ItensInstallByStealReasonCriteriaDTO criteriaDTO) throws DepartmentNotFoundException {
        if(criteriaDTO.getResponsibleCode() != null)
            departmentService.findById(criteriaDTO.getResponsibleCode());
        Utils.isPeriodInvalid(criteriaDTO.getInitialPeriod(), criteriaDTO.getFinalPeriod());
    }
}
