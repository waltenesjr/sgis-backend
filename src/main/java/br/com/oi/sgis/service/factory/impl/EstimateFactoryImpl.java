package br.com.oi.sgis.service.factory.impl;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.Estimate;
import br.com.oi.sgis.entity.ItemEstimate;
import br.com.oi.sgis.entity.TicketIntervention;
import br.com.oi.sgis.mapper.EstimateMapper;
import br.com.oi.sgis.mapper.ItemEstimateMapper;
import br.com.oi.sgis.repository.EstimateRepository;
import br.com.oi.sgis.repository.ItemEstimateRepository;
import br.com.oi.sgis.repository.TicketInterventionRepository;
import br.com.oi.sgis.service.factory.EstimateFactory;
import br.com.oi.sgis.service.factory.TicketInterventionFactory;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstimateFactoryImpl implements EstimateFactory {
    private static final EstimateMapper estimateMapper = EstimateMapper.INSTANCE;
    private static final ItemEstimateMapper itemEstimateMapper = ItemEstimateMapper.INSTANCE;
    private final EstimateRepository estimateRepository;
    private final ItemEstimateRepository itemEstimateRepository;
    private final TicketInterventionRepository ticketInterventionRepository;
    private final Validator<EstimateDTO> validator;
    private final TicketInterventionFactory ticketInterventionFactory;

    @Override @Transactional(rollbackFor = IllegalArgumentException.class)
    public Estimate createEstimate(EstimateDTO estimateDTO) {
        validator.validate(estimateDTO);
        Estimate estimate = estimateMapper.toModel(estimateDTO);
        List<ItemEstimate> itemsToSave = estimate.getItemEstimates();
        estimate.setItemEstimates(null);
        Estimate estimateSaved;
        try {
            String estimateNumber = generateEstimateNumber(estimate.getDepartment().getId().substring(0,2));
            estimate.setId(estimateNumber);
            if(estimate.getItemEstimates()!=null) {
                estimate.getItemEstimates().forEach(i -> i.getId().setEstimate(estimate));
            }
            estimateSaved =  estimateRepository.save(estimate);
        }catch (RuntimeException e){
            throw new IllegalArgumentException(MessageUtils.ESTIMATE_SAVE_ERROR.getDescription());
        }
        createItemEstimate(estimateSaved, itemsToSave);
        return estimateSaved;

    }

    @Override
    public void createItemEstimate(Estimate estimateSaved, List<ItemEstimate> itemEstimate) {
        if(itemEstimate==null || itemEstimate.isEmpty())
            return;
        itemEstimate.forEach(ie -> saveItemEstimate(estimateSaved, ie));

    }

    private void saveItemEstimate(Estimate estimateSaved, ItemEstimate ie) {
        ie.getId().setEstimate(estimateSaved);
        String situationId = estimateSaved.getContact() == null ? "EOR": "ECT";
        TicketInterventionDTO ticketInterventionDTO = getTicketInterventionDTO(estimateSaved, ie, situationId);
        TicketIntervention ticketIntervention = ticketInterventionFactory.createTicketIntervention(ticketInterventionDTO);
        TicketIntervention ticketInterventionSaved = ticketInterventionRepository.save(ticketIntervention);
        ie.getId().setTicketIntervention(ticketInterventionSaved);
        itemEstimateRepository.save(ie);
    }

    private TicketInterventionDTO getTicketInterventionDTO(Estimate estimateSaved, ItemEstimate ie, String situationId) {
        return TicketInterventionDTO.builder()
                .repSituation(SituationDTO.builder().id(situationId).build())
                .repairTicket(RepairTicketDTO.builder().brNumber(ie.getId().getTicketIntervention().getId().getRepairTicket().getId()).build())
                .intervention(InterventionDTO.builder().id(ie.getId().getTicketIntervention().getIntervention().getId()).build()).companyEstimate(estimateSaved.getCompany().getId())
                .observation(ie.getId().getTicketIntervention().getObservation())
                .unity(UnityDTO.builder().id(ie.getId().getTicketIntervention().getUnity().getId()).build())
                .externalRepair(true).build();
    }

    private String generateEstimateNumber(String uf) {
        String lastId =  estimateRepository.findTop1ByIdDesc(uf);
        DecimalFormat df = new DecimalFormat("000000");
        String year;
        String lastNumber;
        if(lastId == null){
            year = Year.now().toString();
            lastNumber = df.format(1);
        }else {
            year = lastId.substring(2,6);
            lastNumber = lastId.substring(6);
        }
        if(!Year.now().toString().equals(year)) {
            year = Year.now().toString();
            lastNumber = df.format(1);
        }else {
            lastNumber = df.format(new BigInteger(lastNumber).add(BigInteger.ONE));
        }
        return uf + year + lastNumber;
    }
}
