package br.com.oi.sgis.repository;


import br.com.oi.sgis.dto.ItensInstallByStealReasonCriteriaDTO;
import br.com.oi.sgis.dto.ItensInstallByStealReasonDTO;

import java.util.List;

public interface ItensByStealReasonRepository {

    List<ItensInstallByStealReasonDTO> findByParamsSteal(ItensInstallByStealReasonCriteriaDTO criteriaDTO);
}
