package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.SituationDTO;
import br.com.oi.sgis.entity.Situation;
import br.com.oi.sgis.enums.ReasonForWriteOffEnum;
import br.com.oi.sgis.enums.SituationEnum;
import br.com.oi.sgis.exception.SituationNotFoundException;
import br.com.oi.sgis.mapper.SituationMapper;
import br.com.oi.sgis.repository.SituationRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SituationService {

    private final SituationRepository situationRepository;
    private final DepartmentService departmentService;
    private static final SituationMapper situationMapper = SituationMapper.INSTANCE;

    private static final String MOCKED_DEPARTMENT_USER = "RJ-OI-ARC";

    public PaginateResponseDTO<SituationDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(situationRepository.findAll(paging).map(situationMapper::toDTO));

        return PageableUtil.paginate(situationRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(situationMapper::toDTO));
    }

    public MessageResponseDTO conciliate(SituationDTO situationDTO) throws SituationNotFoundException {
        verifyIfExists(situationDTO.getId());
        try{
            situationRepository.save(situationMapper.toModel(situationDTO));
            return MessageResponseDTO.builder()
                    .message(MessageUtils.SITUATION_CONCILIATE.getDescription() + situationDTO.getId())
                    .title("Sucesso!").status(HttpStatus.OK).build();
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.SITUATION_CONCILIATE_ERROR.getDescription());
        }
    }

    public List<SituationDTO> listAll(){
        List<Situation> allSituations = situationRepository.findAll();
        return allSituations.parallelStream().map(situationMapper::toDTO).collect(Collectors.toList());
    }

    public List<SituationDTO> listAllCRP(){
        List<String> crpSituations = List.of(SituationEnum.DEF.getCod(), SituationEnum.DIS.getCod());
        List<Situation> allSituations = situationRepository.findCRPSituations(crpSituations);
        return allSituations.parallelStream().map(situationMapper::toDTO).collect(Collectors.toList());
    }

    public List<SituationDTO> listAllToUpdateUnity(){
        List<String> situationsToUpdateUnity = List.of(SituationEnum.DIS.getCod(),SituationEnum.DEF.getCod(),
                SituationEnum.OFE.getCod(), SituationEnum.RES.getCod());
        List<Situation> allSituations = situationRepository.findCRPSituations(situationsToUpdateUnity);
        return allSituations.parallelStream().map(situationMapper::toDTO).collect(Collectors.toList());
    }

    public SituationDTO findById(String id) throws SituationNotFoundException{
        Situation situation = verifyIfExists(id);
        return situationMapper.toDTO(situation);
    }

    @SneakyThrows
    public SituationDTO selectSituationCUS(String idResponsible) {
        DepartmentDTO responsible = departmentService.findById(idResponsible);
        if(responsible.getId().equals(MOCKED_DEPARTMENT_USER)){
            return findById(SituationEnum.DIS.getCod());
        }
        return  findById(SituationEnum.TRN.getCod());
    }

    @SneakyThrows
    public SituationDTO situationsByUnityWriteOffReason(ReasonForWriteOffEnum reason) {
        return findById(reason.getSituation());
    }

    private Situation verifyIfExists(String id) throws SituationNotFoundException {
        return situationRepository.findById(id)
                .orElseThrow(()-> new SituationNotFoundException(MessageUtils.SITUATION_NOT_FOUND_BY_ID.getDescription() + id ));
    }
}
