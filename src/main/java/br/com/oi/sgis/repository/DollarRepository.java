package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Dollar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface DollarRepository extends JpaRepository<Dollar, LocalDateTime> {
    Page<Dollar> findDollarByValueEquals(BigDecimal value, Pageable paging);
    Page<Dollar> findDollarByDateEquals(LocalDateTime date, Pageable paging);

}
