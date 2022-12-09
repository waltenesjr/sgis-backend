package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.EquipamentTypeRepairDTO;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EquipmentTypeRepairRepositoryCustom {
    List<EquipamentTypeRepairDTO> findByRepairCenter(String repairCenter, LocalDateTime initialDate, LocalDateTime finalDate);

}
