package br.com.oi.sgis.service.factory;

import br.com.oi.sgis.dto.EstimateDTO;
import br.com.oi.sgis.entity.Estimate;
import br.com.oi.sgis.entity.ItemEstimate;

import java.util.List;

public interface EstimateFactory {
    Estimate createEstimate(EstimateDTO estimateDTO);
    void createItemEstimate(Estimate estimateSaved, List<ItemEstimate> itemEstimate);
}
