package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.ElectricalPropEquip;
import br.com.oi.sgis.entity.ElectricalPropEquipID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectricalPropEquipRepository extends JpaRepository<ElectricalPropEquip, ElectricalPropEquipID> {
}
