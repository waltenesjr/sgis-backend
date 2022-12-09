package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.GenericQueryItem;
import br.com.oi.sgis.entity.GenericQueryItemID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenericQueryItemRepository extends JpaRepository<GenericQueryItem, GenericQueryItemID> {
}
