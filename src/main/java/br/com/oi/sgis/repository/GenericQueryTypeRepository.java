package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.GenericQueryType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenericQueryTypeRepository extends JpaRepository<GenericQueryType, String> {
}
