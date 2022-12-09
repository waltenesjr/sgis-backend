package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.MovItensReportDTO;
import br.com.oi.sgis.dto.MovItensViewReportDTO;
import br.com.oi.sgis.entity.view.MovItensView;
import br.com.oi.sgis.enums.MovItensReportOrderEnum;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.repository.MovItensRepository;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.ReportUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MovItensService {
    private final MovItensRepository movItensRepository;
    private final Validator<MovItensReportDTO> movItensReportDTOValidator;
    private final ReportService reportService;


    @SneakyThrows
    public byte[] report(MovItensReportDTO movItensReportDTO, TypeDocEnum type) throws JRException {
        movItensReportDTOValidator.validate(movItensReportDTO);
        List<MovItensViewReportDTO> viewReport = listItens(movItensReportDTO);
        String file = "/reports/movimentacaoReportExcel.jrxml";
        if(type.getCod().equals(TypeDocEnum.PDF.getCod()))
            file = "/reports/movimentacaoReport.jrxml";
        JasperReport jasperReport = ReportUtils.getJasperReport(file);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("subtotais", movItensReportDTO.isBreakTotals());
        parameters.put("totalGeral", viewReport.stream().mapToInt(MovItensViewReportDTO::getTotalItens).sum());
        return reportService.fillMovItensReport(viewReport, jasperReport, parameters, type);

    }

    private List<MovItensViewReportDTO> listItens(MovItensReportDTO movItensReportDTO) {
        List<MovItensView> itens =  movItensRepository.findByParameters(movItensReportDTO);
        if(itens.isEmpty()){
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        }
        return getItens(movItensReportDTO, itens);
    }

    private List<MovItensViewReportDTO> getItens(MovItensReportDTO movItensReportDTO, List<MovItensView> itens) {
        Map<String, List<MovItensView>> map;
        if(!movItensReportDTO.isBreakTotals()){
            map = new HashMap<>();
            map.put("", itens);
        }else{
            map = getStringListMap(itens, movItensReportDTO.getOrderBy());
        }
        List<MovItensViewReportDTO> viewReport = new ArrayList<>();
        map.values().forEach(m-> viewReport.add(MovItensViewReportDTO.builder().groupItens(m).totalItens(m.size()).build()));
        return viewReport;
    }

    private Map<String, List<MovItensView>> groupByDate(List<MovItensView> itens) {
        Map<String, List<MovItensView>> map;
        map = itens.stream()
                .collect(groupingBy(
                        MovItensView::getDate,
                        LinkedHashMap::new,
                        Collectors.toList()));
        return map;
    }
    private Map<String, List<MovItensView>> groupByUnityCode(List<MovItensView> itens) {
        Map<String, List<MovItensView>> map;
        map = itens.stream()
                .collect(groupingBy(MovItensView::getUnityCode, LinkedHashMap::new,
                        Collectors.toList()));
        return map;
    }

    private Map<String, List<MovItensView>> groupByResponsibleFrom(List<MovItensView> itens) {
        Map<String, List<MovItensView>> map;
        map = itens.stream()
                .collect(groupingBy(MovItensView::getFromResponsible, LinkedHashMap::new,
                        Collectors.toList()));
        return map;
    }

    private Map<String, List<MovItensView>> groupByResponsibleTo(List<MovItensView> itens) {
        Map<String, List<MovItensView>> map;
        map = itens.stream()
                .collect(groupingBy(MovItensView::getToResponsible, LinkedHashMap::new,
                        Collectors.toList()));
        return map;
    }
    private Map<String, List<MovItensView>> groupBySituationFrom(List<MovItensView> itens) {
        Map<String, List<MovItensView>> map;
        map = itens.stream()
                .collect(groupingBy(MovItensView::getFromSituationCode,LinkedHashMap::new,
                        Collectors.toList()));
        return map;
    }

    private Map<String, List<MovItensView>> groupBySituationTo(List<MovItensView> itens) {
        Map<String, List<MovItensView>> map;
        map = itens.stream()
                .collect(groupingBy(MovItensView::getToSituationCode,LinkedHashMap::new,
                        Collectors.toList()));
        return map;
    }

    private Map<String, List<MovItensView>> groupByTechnicianFrom(List<MovItensView> itens) {
        Map<String, List<MovItensView>> map;
        map = itens.stream()
                .collect(groupingBy(MovItensView::getFromTechnician,LinkedHashMap::new,
                        Collectors.toList()));
        return map;
    }

    private Map<String, List<MovItensView>> groupByTechnicianTo(List<MovItensView> itens) {
        Map<String, List<MovItensView>> map;
        map = itens.stream()
                .collect(groupingBy(MovItensView::getToTechnician,LinkedHashMap::new,
                        Collectors.toList()));
        return map;
    }
    private Map<String, List<MovItensView>> getStringListMap(List<MovItensView> itens, MovItensReportOrderEnum orderEnum) {
        switch (orderEnum){
            case DE_SIGLA_PROP: return groupByResponsibleFrom(itens);
            case PARA_SIGLA_PROP:return groupByResponsibleTo(itens);
            case PARA_COD_PLACA: return groupByUnityCode(itens);
            case DE_TECNICO: return groupByTechnicianFrom(itens);
            case PARA_TECNICO: return groupByTechnicianTo(itens);
            case DE_COD_SIT: return groupBySituationFrom(itens);
            case PARA_COD_SIT: return groupBySituationTo(itens);
            default: return groupByDate(itens);
        }
    }




}
