package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.ModelContract;
import br.com.oi.sgis.entity.ModelContractID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelContractRepository extends JpaRepository<ModelContract, ModelContractID> {
}
