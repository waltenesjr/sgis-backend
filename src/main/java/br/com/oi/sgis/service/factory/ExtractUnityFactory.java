package br.com.oi.sgis.service.factory;

import br.com.oi.sgis.dto.UnitExtractionReportDTO;
import br.com.oi.sgis.entity.Unity;

public interface ExtractUnityFactory {
    UnitExtractionReportDTO createExtractUnity(Unity unity);
}
