package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.BarcodeFAS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarcodeFASRepository extends JpaRepository<BarcodeFAS, String> {
}
