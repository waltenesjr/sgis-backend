package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.ItemBySitReportCriteriaDTO;
import br.com.oi.sgis.dto.ItemBySituationViewDTO;
import br.com.oi.sgis.dto.ItemBySituationViewReportDTO;
import br.com.oi.sgis.enums.ItemBySitReportTypeEnum;
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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ItemBySituationService {

    private final UnityRepository situationItemRepository;
    private final Validator<ItemBySitReportCriteriaDTO> validator;
    private final ReportService reportService;


    private List<ItemBySituationViewReportDTO> listItens(ItemBySitReportCriteriaDTO criteriaDTO){
        validator.validate(criteriaDTO);
        List<ItemBySituationViewDTO> itens = situationItemRepository.findSituationByParams(criteriaDTO);
        if (itens.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        LinkedHashMap<String, List<ItemBySituationViewDTO>> mapValuesByDepto = mapValuesByDpTO(itens);
        try {
            return collectViewReport(mapValuesByDepto);
        }catch (RuntimeException e){
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }


    @SneakyThrows
    public byte[] report(ItemBySitReportCriteriaDTO itemBySitReportCriteriaDTO, TypeDocEnum type) {
        List<ItemBySituationViewReportDTO> itens = listItens(itemBySitReportCriteriaDTO);
        String file = "/reports/itensBySituationReportExcel.jrxml";
        if(type.getCod().equals(TypeDocEnum.PDF.getCod()))
            file = "/reports/itensBySituationReport.jrxml";
        JasperReport jasperReport = ReportUtils.getJasperReport(file);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("isAnalitic", itemBySitReportCriteriaDTO.getReportType().equals(ItemBySitReportTypeEnum.ANALITICO));
        return reportService.fillSitItensReport(itens, jasperReport, parameters, type);

    }

    private List<ItemBySituationViewReportDTO> collectViewReport(LinkedHashMap<String, List<ItemBySituationViewDTO>> mapValuesByDepto) {
        return mapValuesByDepto.entrySet().parallelStream().map(m->{
            ItemBySituationViewReportDTO item = ItemBySituationViewReportDTO.builder().department(m.getKey()).build();
            item.setAnaliticList(m.getValue());
            item.setTotalByDepartment(getTotalByDepartment(m.getValue()));
            return item;
        }).collect(toList());
    }

    private ItemBySituationViewDTO getTotalByDepartment(List<ItemBySituationViewDTO> value) {
        ItemBySituationViewDTO itemTotal = ItemBySituationViewDTO.builder().build();
        itemTotal.setTrn(value.parallelStream().map(ItemBySituationViewDTO::getTrn).reduce(0L, Long::sum));
        itemTotal.setDis(value.parallelStream().map(ItemBySituationViewDTO::getDis).reduce(0L, Long::sum));
        itemTotal.setEmp(value.parallelStream().map(ItemBySituationViewDTO::getEmp).reduce(0L, Long::sum));
        itemTotal.setTdr(value.parallelStream().map(ItemBySituationViewDTO::getTdr).reduce(0L, Long::sum));
        itemTotal.setEmu(value.parallelStream().map(ItemBySituationViewDTO::getEmu).reduce(0L, Long::sum));
        itemTotal.setTrr(value.parallelStream().map(ItemBySituationViewDTO::getTrr).reduce(0L, Long::sum));
        itemTotal.setBxp(value.parallelStream().map(ItemBySituationViewDTO::getBxp).reduce(0L, Long::sum));
        itemTotal.setTrp(value.parallelStream().map(ItemBySituationViewDTO::getTrp).reduce(0L, Long::sum));
        itemTotal.setTre(value.parallelStream().map(ItemBySituationViewDTO::getTre).reduce(0L, Long::sum));
        itemTotal.setRep(value.parallelStream().map(ItemBySituationViewDTO::getRep).reduce(0L, Long::sum));
        itemTotal.setBxu(value.parallelStream().map(ItemBySituationViewDTO::getBxu).reduce(0L, Long::sum));
        itemTotal.setBxe(value.parallelStream().map(ItemBySituationViewDTO::getBxe).reduce(0L, Long::sum));
        itemTotal.setDvr(value.parallelStream().map(ItemBySituationViewDTO::getDvr).reduce(0L, Long::sum));
        itemTotal.setTrd(value.parallelStream().map(ItemBySituationViewDTO::getTrd).reduce(0L, Long::sum));
        itemTotal.setUso(value.parallelStream().map(ItemBySituationViewDTO::getUso).reduce(0L, Long::sum));
        itemTotal.setBxi(value.parallelStream().map(ItemBySituationViewDTO::getBxi).reduce(0L, Long::sum));
        itemTotal.setPre(value.parallelStream().map(ItemBySituationViewDTO::getPre).reduce(0L, Long::sum));
        itemTotal.setBxc(value.parallelStream().map(ItemBySituationViewDTO::getBxc).reduce(0L, Long::sum));
        itemTotal.setBxo(value.parallelStream().map(ItemBySituationViewDTO::getBxo).reduce(0L, Long::sum));
        itemTotal.setRes(value.parallelStream().map(ItemBySituationViewDTO::getRes).reduce(0L, Long::sum));
        itemTotal.setOfe(value.parallelStream().map(ItemBySituationViewDTO::getOfe).reduce(0L, Long::sum));
        itemTotal.setDef(value.parallelStream().map(ItemBySituationViewDTO::getDef).reduce(0L, Long::sum));
        itemTotal.setTdd(value.parallelStream().map(ItemBySituationViewDTO::getTdd).reduce(0L, Long::sum));
        itemTotal.setTotal(value.parallelStream().map(ItemBySituationViewDTO::getTotal).reduce(0L, Long::sum));
        return itemTotal;
    }

    private LinkedHashMap<String, List<ItemBySituationViewDTO>> mapValuesByDpTO(List<ItemBySituationViewDTO> itens) {
        return itens.parallelStream().collect(groupingBy(ItemBySituationViewDTO::getDepartmentCode, LinkedHashMap::new, toList()));
    }
}
