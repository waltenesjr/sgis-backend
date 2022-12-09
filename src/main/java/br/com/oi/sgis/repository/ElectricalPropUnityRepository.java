package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.ElectricalPropUnity;
import br.com.oi.sgis.entity.ElectricalPropUnityID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectricalPropUnityRepository extends JpaRepository<ElectricalPropUnity, ElectricalPropUnityID> {
}
