package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.repository.ParameterRepository;
import br.com.oi.sgis.service.factory.LabelFactory;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.Utils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class LabelService {

    private final TechnicalStaffService technicalStaffService;
    private final ReportService reportService;
    private final LabelFactory labelFactory;
    private final ParameterRepository parameterRepository;

    public LabelParametersDTO getLastBoxLabel(){
        ParameterDTO parameterDTO = getLastParameterUser();
        String lastBoxCode = parameterDTO.getBoxCode();
        validateLabel(parameterDTO, lastBoxCode);

        return LabelParametersDTO.builder().lastEmittedLabel(lastBoxCode)
                .topMargin(5L).lateralMargin(2L).labelHeight(20L).labelWidth(85L)
                .verticalRange(1L).horizontalRange(1L).onlyTest(Boolean.TRUE).build();
    }

    public LabelParametersDTO getLastItemLabel(){
        ParameterDTO parameterDTO = getLastParameterUser();
        String lastBarcode = parameterDTO.getBarcode();
        validateLabel(parameterDTO, lastBarcode);

        return LabelParametersDTO.builder().lastEmittedLabel(lastBarcode)
                .topMargin(0L).lateralMargin(2L).labelHeight(11L).labelWidth(48L)
                .verticalRange(1L).horizontalRange(1L).onlyTest(Boolean.TRUE).build();

    }

    private void validateLabel(ParameterDTO parameterDTO, String lastEmittedLabel) {
        if(!parameterDTO.getPrefix().equals(lastEmittedLabel.substring(2,4)))
            throw new IllegalArgumentException(MessageUtils.LABEL_HOLDING_ERROR.getDescription());
        if(lastEmittedLabel.length()<15)
            throw new IllegalArgumentException(MessageUtils.LABEL_SIZE_ERROR.getDescription());
    }

    @SneakyThrows
    private ParameterDTO getLastParameterUser(){
        TechnicalStaffDTO user = technicalStaffService.findById(Utils.getUser().getId());
        return user.getCgcCpfCompany();
    }

    @SneakyThrows
    public byte[] boxLabels(LabelParametersDTO labelParametersDTO) {
        try {
            ParameterDTO parameterDTO = getLastParameterUser();
            String lastBoxCode = parameterDTO.getBoxCode();
            List<String> nextLabels = new ArrayList<>();
            lastBoxCode = labelFactory.generateNextLabelsValues(labelParametersDTO, lastBoxCode, nextLabels);

            List<LabelReportDTO> labels;
            labels = labelFactory.getLabels(labelParametersDTO, nextLabels);
            Map<String, Object> parameters = new HashMap<>();

            if (Boolean.FALSE.equals(labelParametersDTO.getOnlyTest()))
                parameterRepository.updateBoxCode(lastBoxCode, parameterDTO.getId());
            return reportService.labelReport(labels, parameters);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

    @SneakyThrows
    public byte[] itemLabels(LabelParametersDTO labelParametersDTO) {
        try {
            ParameterDTO parameterDTO = getLastParameterUser();
            String lastBarcode = parameterDTO.getBarcode();
            List<String> nextLabels = new ArrayList<>();
            lastBarcode = labelFactory.generateNextLabelsValues(labelParametersDTO, lastBarcode, nextLabels);

            List<LabelReportDTO> labels;
            labels = labelFactory.getLabels(labelParametersDTO, nextLabels);
            Map<String, Object> parameters = new HashMap<>();

            if(Boolean.FALSE.equals(labelParametersDTO.getOnlyTest()))
                parameterRepository.updateBarcode(lastBarcode, parameterDTO.getId());

            return reportService.labelReport(labels, parameters);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }

    }

    public PackingLabelParametersDTO getPackingLabel(){
        ParameterDTO parameterDTO = getLastParameterUser();
        String lastBarcode = parameterDTO.getBarcode();
        validateLabel(parameterDTO, lastBarcode);

        return PackingLabelParametersDTO.builder()
                .topMargin(5L).lateralMargin(2L).labelHeight(20L).labelWidth(85L)
                .verticalRange(1L).horizontalRange(1L).inhibitBarcode(Boolean.FALSE).build();

    }

    @SneakyThrows
    public byte[] packingLabel(PackingLabelParametersDTO parametersDTO) {
        try {
            List<LabelReportDTO> labels;
            labels = labelFactory.getLabelsPacking(parametersDTO,parametersDTO.getBarcodes());
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("inabilita", parametersDTO.getInhibitBarcode());

            return reportService.labelPackingReport(labels, parameters);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }

    }
}
