package br.com.oi.sgis.service.factory;

import br.com.oi.sgis.dto.TicketInterventionDTO;
import br.com.oi.sgis.entity.TicketIntervention;

public interface TicketInterventionFactory {
    TicketIntervention createTicketIntervention(TicketInterventionDTO ticketInterventionDTO);
}
