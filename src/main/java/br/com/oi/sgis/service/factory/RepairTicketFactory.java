package br.com.oi.sgis.service.factory;

import br.com.oi.sgis.dto.RepairTicketDTO;
import br.com.oi.sgis.entity.RepairTicket;
import br.com.oi.sgis.exception.RepairTicketException;

public interface RepairTicketFactory {
    RepairTicket createRepairTicket(RepairTicketDTO repairTicketDTO) throws RepairTicketException;
}
