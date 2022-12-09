package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.ItemBySitReportCriteriaDTO;
import br.com.oi.sgis.dto.ItemBySituationViewDTO;

import java.util.List;

public interface SituationItemRepository {
    List<ItemBySituationViewDTO> findSituationByParams(ItemBySitReportCriteriaDTO criteriaDTO);
}
