package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Uf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UfRepository extends JpaRepository<Uf, String> {
}
