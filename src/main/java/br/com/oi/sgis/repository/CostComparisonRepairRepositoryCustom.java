package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.CostComparisonRepDTO;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CostComparisonRepairRepositoryCustom {
    List<CostComparisonRepDTO> findCostRepair(String repairCenter, LocalDateTime initialDate, LocalDateTime finalDate);
}
