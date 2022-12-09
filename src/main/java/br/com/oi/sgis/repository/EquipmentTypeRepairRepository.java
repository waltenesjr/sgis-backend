package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.EquipamentTypeRepairDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentTypeRepairRepository extends JpaRepository<EquipamentTypeRepairDTO, Long>, EquipmentTypeRepairRepositoryCustom {
}
