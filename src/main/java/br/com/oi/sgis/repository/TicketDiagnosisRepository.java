package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.TicketDiagnosis;
import br.com.oi.sgis.entity.TicketDiagnosisID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketDiagnosisRepository extends JpaRepository<TicketDiagnosis, TicketDiagnosisID> {

    @Query(value = "select td from TicketDiagnosis td where td.id.ticketIntervention.id.sequence in (:sequences) ")
    List<TicketDiagnosis> findAllByTicketIntervention(@Param("sequences") List<Long> sequences);

}
