package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.StockSummaryCriteriaDTO;
import br.com.oi.sgis.dto.StockSummaryDTO;

import java.util.List;

public interface StockSummaryRepository {

    List<StockSummaryDTO> findBySummaryParams(StockSummaryCriteriaDTO stockSummaryCriteriaDTO);
}
