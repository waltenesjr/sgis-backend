package br.com.oi.sgis.service.factory.impl;

import br.com.oi.sgis.dto.LabelParametersDTO;
import br.com.oi.sgis.dto.LabelReportDTO;
import br.com.oi.sgis.dto.PackingLabelParametersDTO;
import br.com.oi.sgis.dto.UnityDTO;
import br.com.oi.sgis.enums.LabelPrintTypeEnum;
import br.com.oi.sgis.service.UnityService;
import br.com.oi.sgis.service.factory.LabelFactory;
import br.com.oi.sgis.util.LabelUtils;
import lombok.RequiredArgsConstructor;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static br.com.oi.sgis.util.Utils.isEven;

@Service
@RequiredArgsConstructor
public class LabelFactoryImpl implements LabelFactory {

    private final UnityService unityService;

    @Override
    public String generateNextLabelsValues(LabelParametersDTO labelParametersDTO, String lastBarcode, List<String> nextLabels) {
        for (int i = 0; i < labelParametersDTO.getLabelQuantity(); i++) {
            String nextLabel = nextLabel(lastBarcode);
            lastBarcode = nextLabel;
            nextLabels.add(nextLabel);
            if (labelParametersDTO.getPrintType()!= null && labelParametersDTO.getPrintType().equals(LabelPrintTypeEnum.DUPLICADO))
                nextLabels.add(nextLabel);
        }
        return lastBarcode;
    }


    @Override
    public List<LabelReportDTO> getLabels(LabelParametersDTO labelParametersDTO, List<String> nextLabels) throws OutputException, BarcodeException, IOException {
        List<LabelReportDTO> labels;
        if(labelParametersDTO.getPrintType() == null){
            labels = labelsByRow(nextLabels, labelParametersDTO);
        }else {
            labels = labelsByColumn(nextLabels, labelParametersDTO);
        }
        return labels;
    }

    @Override
    public List<LabelReportDTO> getLabelsPacking(PackingLabelParametersDTO parameters, List<String> nextLabels) throws OutputException, BarcodeException, IOException {
        List<LabelReportDTO> labels = labelsByRow(nextLabels, new LabelParametersDTO(parameters));
        for (LabelReportDTO label: labels
        ) {
            if(label.getBarcode1()!=null)
                label.setBarcode1(getDesciptionPacking(label.getBarcode1(), parameters.getInhibitBarcode()));
            if(label.getBarcode2()!=null)
                label.setBarcode2(getDesciptionPacking(label.getBarcode2(), parameters.getInhibitBarcode()));
            if(label.getBarcode3()!=null)
                label.setBarcode3(getDesciptionPacking(label.getBarcode2(), parameters.getInhibitBarcode()));
        }
        return labels;
    }

    private String getDesciptionPacking(String barcode, Boolean inhibit ) {
        UnityDTO unityDTO = unityService.findById(barcode);
        StringBuilder desctription = new StringBuilder();

        if(Boolean.FALSE.equals(inhibit))
            desctription.append(barcode).append("\n");

        desctription.append(unityDTO.getUnityCode().getId()).append(" - ")
                .append(unityDTO.getUnityCode().getDescription()).append(" - ")
                .append(unityDTO.getUnityCode().getMnemonic())
                .append("\n").append(unityDTO.getDeposit().getId()).append(" - ")
                .append(unityDTO.getStation()!=null ? unityDTO.getStation().getId() : "")
                .append(unityDTO.getLocation()!=null ? unityDTO.getLocation() : ""). append(" - ");
        return desctription.toString();
    }

    private List<LabelReportDTO> labelsByColumn(List<String> nextLabels, LabelParametersDTO labelParametersDTO) throws OutputException, BarcodeException, IOException {
        List<LabelReportDTO> labels = new ArrayList<>();
        switch (labelParametersDTO.getLabelByLine()){
            case 1:
                for (String label: nextLabels
                ) {
                    LabelReportDTO labelReportDTO = LabelReportDTO.builder().column1(
                            LabelUtils.getBarcodePersonalized(labelParametersDTO, label, labelParametersDTO.getOnlyTest())
                    ).barcode1(label).build();
                    labels.add(labelReportDTO);
                }
                break;
            case 2:
                for (String label: nextLabels
                ) {
                    LabelReportDTO labelReportDTO = LabelReportDTO.builder().build();
                    setColumn2(labelParametersDTO, label, labelReportDTO);
                    labelReportDTO.setColumn1(LabelUtils.getNoBarcodeOnTest(labelParametersDTO));
                    labels.add(labelReportDTO);
                }
                break;
            default:
                for (String label: nextLabels
                ) {
                    LabelReportDTO labelReportDTO = LabelReportDTO.builder().build();
                    setColumn3(labelParametersDTO, label, labelReportDTO);
                    labelReportDTO.setColumn1(LabelUtils.getNoBarcodeOnTest(labelParametersDTO));
                    labelReportDTO.setColumn2(LabelUtils.getNoBarcodeOnTest(labelParametersDTO));
                    labels.add(labelReportDTO);
                }
        }
        return labels;
    }

    private List<LabelReportDTO> labelsByRow(List<String> nextLabels, LabelParametersDTO labelParametersDTO) throws OutputException, BarcodeException, IOException {
        List<LabelReportDTO> labels = new ArrayList<>();
        LabelReportDTO labelAux = LabelReportDTO.builder().build();
        if(labelParametersDTO.getLabelByLine()==1){
            return labelsByColumn(nextLabels, labelParametersDTO);
        }
        for (String label: nextLabels
        ) {
            if(labelAux.getColumn1()==null){
                setColumn1(labelParametersDTO,label, labelAux);
            }else if(labelAux.getColumn2() == null && labelParametersDTO.getLabelByLine()>1) {
                setColumn2(labelParametersDTO, label, labelAux);
                if(labelParametersDTO.getLabelByLine()==2){
                    labels.add(labelAux);
                    labelAux = LabelReportDTO.builder().build();
                }

            }
            else if(labelAux.getColumn3() == null && labelParametersDTO.getLabelByLine()>2) {
                setColumn3(labelParametersDTO, label, labelAux);
                labels.add(labelAux);
                labelAux = LabelReportDTO.builder().build();
            }
        }
        if(nextLabels.size()%3 != 0 || nextLabels.size()% labelParametersDTO.getLabelByLine() !=0){
            labels.add(labelAux);
        }
        return labels;
    }

    private void setColumn3( LabelParametersDTO labelParametersDTO, String label, LabelReportDTO labelReportDTO) throws BarcodeException, OutputException, IOException {
        labelReportDTO.setColumn3(LabelUtils.getBarcodePersonalized(labelParametersDTO, label, labelParametersDTO.getOnlyTest()));
        labelReportDTO.setBarcode3(label);
    }

    private void setColumn2( LabelParametersDTO labelParametersDTO, String label, LabelReportDTO labelReportDTO) throws BarcodeException, OutputException, IOException {
        labelReportDTO.setColumn2(LabelUtils.getBarcodePersonalized(labelParametersDTO, label, labelParametersDTO.getOnlyTest()));
        labelReportDTO.setBarcode2(label);
    }

    private void setColumn1( LabelParametersDTO labelParametersDTO, String label, LabelReportDTO labelReportDTO) throws BarcodeException, OutputException, IOException {
        labelReportDTO.setColumn1(LabelUtils.getBarcodePersonalized(labelParametersDTO, label, labelParametersDTO.getOnlyTest()));
        labelReportDTO.setBarcode1(label);
    }

    private String nextLabel(String lastLabel) {
        DecimalFormat df = new DecimalFormat("00000000000000000000000");
        String numberLabel = lastLabel.substring(6,15);
        String lastLabelNumber = df.format(new BigInteger(numberLabel).add(BigInteger.ONE));
        String seq = lastLabelNumber.substring(14,23);

        String lastDigit = getLastDigit(lastLabel.substring(2,6)+ seq);
        return lastLabel.substring(0,6)+seq + lastDigit;
    }

    private  String getLastDigit(String code) {
        BigInteger aux = BigInteger.ZERO;
        String result = "0";
        for (int i = 1; i <=code.length() ; i++) {
            BigInteger codeNumber = new BigInteger(code.substring(i - 1, i));
            if(isEven(BigInteger.valueOf(i)))
                aux = aux.add(codeNumber.multiply(BigInteger.ONE));
            else
                aux = aux.add(codeNumber.multiply(BigInteger.valueOf(3)));
        }
        if(aux.mod(BigInteger.TEN).equals(BigInteger.ZERO))
            return result;
        else {
            int i =1;
            while (i<=9){
                if((aux.add(BigInteger.valueOf(i))).mod(BigInteger.TEN).equals(BigInteger.ZERO)) {
                    result = String.valueOf(i); break;
                }
                i++;
            }
        }
        return result;
    }

}
