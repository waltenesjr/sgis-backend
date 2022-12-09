package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.SituationDTO;
import br.com.oi.sgis.entity.RepSituation;
import br.com.oi.sgis.exception.RepSituationNotFoundException;
import br.com.oi.sgis.mapper.RepSituationMapper;
import br.com.oi.sgis.repository.RepSituationRepository;
import br.com.oi.sgis.util.MessageUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RepSituationService {

    private final RepSituationRepository repository;
    private static final RepSituationMapper repSituationMapper = RepSituationMapper.INSTANCE;

    public List<SituationDTO> listAll() {
        List<RepSituation> allRepSituations = repository.findAll();
        return allRepSituations.parallelStream().map(repSituationMapper::toDTO).collect(Collectors.toList());
    }


    public SituationDTO findById(String id) throws RepSituationNotFoundException {
        RepSituation situation = verifyIfExists(id);
        return repSituationMapper.toDTO(situation);
    }

    private RepSituation verifyIfExists(String id) throws RepSituationNotFoundException {
        return repository.findById(id)
                .orElseThrow(()-> new RepSituationNotFoundException(MessageUtils.REP_SITUATION_NOT_FOUND_BY_ID.getDescription() + id ));
    }

    public List<SituationDTO> listForwardRepair() {
        List<RepSituation> allForwardRepairSituations = repository.listSituationsForwardRepair();
        return allForwardRepairSituations.parallelStream().map(repSituationMapper::toDTO).collect(Collectors.toList());
    }

    public List<SituationDTO> listForwardRepairFromInterv() {
        List<String> repSituations = List.of("EGA", "EGC", "EGO",  "ECT", "EOR");
        List<RepSituation> allForwardRepairSituations = repository.listSelectedSituations(repSituations);
        return allForwardRepairSituations.parallelStream().map(repSituationMapper::toDTO).collect(Collectors.toList());
    }
}
