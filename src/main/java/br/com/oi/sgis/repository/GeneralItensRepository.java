package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.GeneralItensCriteriaDTO;
import br.com.oi.sgis.dto.GeneralItensDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneralItensRepository {
    List<GeneralItensDTO> listGeneralItens(GeneralItensCriteriaDTO criteriaDTO);
}
