package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.SummaryItemCriteriaReportDTO;
import br.com.oi.sgis.dto.SummaryItemViewDTO;

import java.util.List;

public interface SummaryItemRepository {
    List<SummaryItemViewDTO> findBySummaryParams(SummaryItemCriteriaReportDTO summaryItemCriteriaReportDTO);
}
