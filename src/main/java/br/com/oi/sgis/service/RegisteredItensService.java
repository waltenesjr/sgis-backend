package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.RegisteredItensCriteriaDTO;
import br.com.oi.sgis.dto.RegisteredItensDTO;
import br.com.oi.sgis.dto.RegisteredItensReportDTO;
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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RegisteredItensService {

    private final UnityRepository registeredItensRepository;
    private final DepartmentService departmentService;
    private final ReportService reportService;

    @SneakyThrows
    private List<RegisteredItensReportDTO> registeredItens(RegisteredItensCriteriaDTO criteriaDTO){
        criteriaValidation(criteriaDTO);
        List<RegisteredItensDTO> itens = registeredItensRepository.findRegisteredItens(criteriaDTO);
        if (itens.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        LinkedHashMap<String, List<RegisteredItensDTO>> mapValuesByNumber = mapValuesByNumber(itens);
        try {
            return collectReport(mapValuesByNumber);
        }catch (RuntimeException e){
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

    @SneakyThrows
    public byte[] report(RegisteredItensCriteriaDTO registeredItensCriteriaDTO, TypeDocEnum type)  {
        List<RegisteredItensReportDTO> itens = registeredItens(registeredItensCriteriaDTO);
        String file = "/reports/registeredItensReportExcel.jrxml";
        if(type.getCod().equals(TypeDocEnum.PDF.getCod()))
            file = "/reports/registeredItensReport.jrxml";
        JasperReport jasperReport = ReportUtils.getJasperReport(file);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("lotacao", Utils.getUser().getDepartmentCode().getId());
        parameters.put("user", Utils.getUser().getId());
        parameters.put("periodo", registeredItensCriteriaDTO.getPeriod());

        return reportService.fillRegisteredItensReport(itens, jasperReport, parameters, type);

    }

    private List<RegisteredItensReportDTO> collectReport(LinkedHashMap<String, List<RegisteredItensDTO>> mapValuesByNumber) {
        return mapValuesByNumber.entrySet().parallelStream().map(m->
             RegisteredItensReportDTO.builder()
                    .registeredItens(m.getValue())
                    .groupBy(m.getKey())
                    .typeGroup(m.getValue().get(0).getNumber()!=null? m.getValue().get(0).getNumberType() : "")
                    .totalItens((long) m.getValue().size()).build()
        ).collect(toList());
    }

    private LinkedHashMap<String, List<RegisteredItensDTO>> mapValuesByNumber(List<RegisteredItensDTO> itens) {
        return itens.parallelStream().collect(groupingBy(m-> m.getNumber()!=null? m.getNumber() : "", LinkedHashMap::new, toList()));
    }

    private void criteriaValidation(RegisteredItensCriteriaDTO criteriaDTO) throws DepartmentNotFoundException {
        if(criteriaDTO.getResponsibleId() != null)
            departmentService.findById(criteriaDTO.getResponsibleId());
        if(criteriaDTO.isFilterByPeriod())
            try {
                Utils.isPeriodInvalid(criteriaDTO.getInitialPeriod(), criteriaDTO.getFinalPeriod());
            }catch (IllegalArgumentException e){
                throw new IllegalArgumentException(MessageUtils.INVALID_DATE.getDescription()); }
        if(criteriaDTO.isFilterByNumber()&& criteriaDTO.getNumber()==null)
            throw new IllegalArgumentException(MessageUtils.INVALID_NUMBER.getDescription());
    }
}
