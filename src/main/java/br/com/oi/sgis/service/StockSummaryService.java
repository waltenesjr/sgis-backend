package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.StockSummaryCriteriaDTO;
import br.com.oi.sgis.dto.StockSummaryDTO;
import br.com.oi.sgis.dto.StockSummaryReportDTO;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.repository.DepartmentUnityRepository;
import br.com.oi.sgis.repository.GyreViewRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.ReportUtils;
import br.com.oi.sgis.util.Utils;
import com.google.common.base.Optional;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class StockSummaryService {

    private final DepartmentService departmentService;
    private final AreaEquipamentService areaEquipamentService;
    private final DepartmentUnityRepository departmentUnityRepository;
    private final GyreViewRepository gyreViewRepository;
    private final ReportService reportService;

    @SneakyThrows
    private List<StockSummaryReportDTO> listStock(StockSummaryCriteriaDTO stockSummaryCriteriaDTO) {
        departmentService.findById(stockSummaryCriteriaDTO.getResponsibleCode());
        if(stockSummaryCriteriaDTO.getModelCode()!=null)
            areaEquipamentService.findById(stockSummaryCriteriaDTO.getModelCode());

        verifyIsAnalisys(stockSummaryCriteriaDTO);
        List<StockSummaryDTO> stockSummaryDTO = departmentUnityRepository.findBySummaryParams(stockSummaryCriteriaDTO);

        if(stockSummaryDTO.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        try {
            return stockSummaryDTO.stream().map(x -> StockSummaryReportDTO.builder()
                    .unity( x.getDescription())
                    .location((x.getStation()!=null ? x.getStation() : "" )+ " - " +  (x.getLocation()!=null ? x.getLocation() : ""))
                    .max(x.getMax()).min(x.getMin()).repos(x.getRepos())
                    .dis(x.getDisponible()).off(x.getOff()).reposition(x.getReposition())
                    .entrance(getEntrance(x, stockSummaryCriteriaDTO)).exit(getExit(x, stockSummaryCriteriaDTO)).build()).collect(Collectors.toList());

        }catch (RuntimeException e){
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

    @SneakyThrows
    public byte[] report(StockSummaryCriteriaDTO stockSummaryCriteriaDTO, TypeDocEnum typeDocEnum){
        List<StockSummaryReportDTO> stockSummaryReportDTO = this.listStock(stockSummaryCriteriaDTO);
        String file = "/reports/StockSummaryReportExcel.jrxml";
        if(typeDocEnum.getCod().equals(TypeDocEnum.PDF.getCod()))
            file = "/reports/stockSummaryReport.jrxml";
        JasperReport jasperReport = ReportUtils.getJasperReport(file);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("departamento", stockSummaryCriteriaDTO.getResponsibleCode());
        return reportService.fillStockSummaryReport(stockSummaryReportDTO, jasperReport, parameters, typeDocEnum);

    }

    private Integer getExit(StockSummaryDTO dtoStock, StockSummaryCriteriaDTO criteria) {
        if(criteria.isAnalysis()) {
            return gyreViewRepository.findExitByUnityCode(dtoStock.getUnity(),
                    criteria.getInitialPeriod(), criteria.getFinalPeriod());
        }
        return null;
    }

    private Integer getEntrance(StockSummaryDTO dtoStock, StockSummaryCriteriaDTO criteria) {
        if(criteria.isAnalysis()) {
            return gyreViewRepository.findEntranceByUnityCode(dtoStock.getUnity(),
                    criteria.getInitialPeriod(), criteria.getFinalPeriod());
        }
        return null;
    }

    private void verifyIsAnalisys(StockSummaryCriteriaDTO stockSummaryCriteriaDTO) {
        if(stockSummaryCriteriaDTO.isAnalysis())
            Utils.isPeriodInvalid(stockSummaryCriteriaDTO.getInitialPeriod(), stockSummaryCriteriaDTO.getFinalPeriod());
    }


}
