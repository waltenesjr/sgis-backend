package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.RegisteredItensCriteriaDTO;
import br.com.oi.sgis.dto.RegisteredItensDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegisteredItensRepository {
    List<RegisteredItensDTO> findRegisteredItens(RegisteredItensCriteriaDTO registeredItensCriteriaDTO);
}
