package br.com.oi.sgis.service.factory;

import br.com.oi.sgis.dto.LabelParametersDTO;
import br.com.oi.sgis.dto.LabelReportDTO;
import br.com.oi.sgis.dto.PackingLabelParametersDTO;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

import java.io.IOException;
import java.util.List;

public interface LabelFactory {
    String generateNextLabelsValues(LabelParametersDTO labelParametersDTO, String lastBarcode, List<String> nextLabels);

    List<LabelReportDTO> getLabels(LabelParametersDTO labelParametersDTO, List<String> nextLabels) throws OutputException, BarcodeException, IOException;
    List<LabelReportDTO> getLabelsPacking(PackingLabelParametersDTO labelParametersDTO, List<String> nextLabels) throws OutputException, BarcodeException, IOException;

}
