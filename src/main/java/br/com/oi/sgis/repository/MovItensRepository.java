package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.view.MovItensView;
import org.springframework.data.jpa.repository.JpaRepository;



public interface MovItensRepository extends JpaRepository<MovItensView, Long>, MovItensRepositoryCustom {

}
