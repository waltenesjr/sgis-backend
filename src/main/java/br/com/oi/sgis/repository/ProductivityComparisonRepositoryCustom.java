package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.ProductivityComparisonDTO;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductivityComparisonRepositoryCustom {

    List<ProductivityComparisonDTO> findProductivityComparisonByTechnical(String repairCenter, String technicalStaffName, LocalDateTime initialDate, LocalDateTime finalDate);
}
