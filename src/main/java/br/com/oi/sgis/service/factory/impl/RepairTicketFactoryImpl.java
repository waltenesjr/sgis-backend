package br.com.oi.sgis.service.factory.impl;

import br.com.oi.sgis.dto.RepairTicketDTO;
import br.com.oi.sgis.entity.RepairTicket;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.exception.RepairTicketException;
import br.com.oi.sgis.exception.UnityNotFoundException;
import br.com.oi.sgis.mapper.RepairTicketMapper;
import br.com.oi.sgis.repository.RepairTicketRepository;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.service.PendencyService;
import br.com.oi.sgis.service.factory.RepairTicketFactory;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.Year;

@Service
@RequiredArgsConstructor
public class RepairTicketFactoryImpl implements RepairTicketFactory {

    private static final RepairTicketMapper repairTicketMapper = RepairTicketMapper.INSTANCE;
    private final Validator<RepairTicket> validator;
    private final PendencyService pendencyService;
    private final RepairTicketRepository repairTicketRepository;
    private final UnityRepository unityRepository;



    @Override @SneakyThrows
    public RepairTicket createRepairTicket(RepairTicketDTO repairTicketDTO) {
        RepairTicket repairTicket = repairTicketMapper.toModel(repairTicketDTO);
        try {
            Unity unity = unityRepository.findById(repairTicket.getUnity().getId())
                    .orElseThrow(()-> new UnityNotFoundException(MessageUtils.UNITY_NOT_FOUND_BY_ID.getDescription() + repairTicket.getUnity().getId()));

            repairTicket.setUnity(unity);
            validator.validate(repairTicket);

            String brNumber = generateBrNumber(repairTicket.getOriginDepartment().getId().substring(0,2));
            repairTicket.setId(brNumber);
            repairTicket.setOpenDate(LocalDateTime.now());
            RepairTicket savedRepairTicket = repairTicketRepository.save(repairTicket);

            if(repairTicketDTO.isGeneratePendency())
                createPendency(savedRepairTicket);

            return savedRepairTicket;
        }catch (RuntimeException e){
            throw new RepairTicketException(MessageUtils.REPAIR_TICKET_ERROR.getDescription());
        }

    }

    private String generateBrNumber(String uf) {
        String lastId =  repairTicketRepository.findTop1ByIdDesc();
        DecimalFormat df = new DecimalFormat("000000");
        String year = lastId.substring(2,6);
        String lastNumber = lastId.substring(6);
        if(!Year.now().toString().equals(year)) {
            year = Year.now().toString();
            lastNumber = df.format(1);
        }else {
            lastNumber = df.format(new BigInteger(lastNumber).add(BigInteger.ONE));
        }
        return uf + year + lastNumber;
    }

    @SneakyThrows
    private void createPendency(RepairTicket repairTicket) {
        if(repairTicket.getDevolutionDepartment().equals(repairTicket.getOriginDepartment()))
            throw new RepairTicketException(MessageUtils.REPAIR_TICKET_PENDENCY_ERROR.getDescription());

        pendencyService.createPendencyFromRepairTicket(repairTicket);

    }


}
