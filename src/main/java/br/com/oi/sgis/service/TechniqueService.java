package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.TechniqueDTO;
import br.com.oi.sgis.entity.Technique;
import br.com.oi.sgis.enums.TechniqueTypeEnum;
import br.com.oi.sgis.exception.TechniqueNotFoundException;
import br.com.oi.sgis.mapper.TechniqueMapper;
import br.com.oi.sgis.repository.TechniqueRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TechniqueService {
    private final TechniqueRepository techniqueRepository;
    private static final TechniqueMapper techniqueMapper = TechniqueMapper.INSTANCE;

    public PaginateResponseDTO<TechniqueDTO> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if (term.isBlank())
            return PageableUtil.paginate(techniqueRepository.findAll(paging).map(techniqueMapper::toDTO));

        return PageableUtil.paginate(techniqueRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(techniqueMapper::toDTO));

    }

    public TechniqueDTO findById(String id) throws TechniqueNotFoundException {
        Technique technique = verifyIfExists(id);
        technique.setTechniqueType(TechniqueTypeEnum.valueOf(technique.getTechniqueType()).getDescription());
        return techniqueMapper.toDTO(technique);
    }

    private Technique verifyIfExists(String id) throws TechniqueNotFoundException {
        return techniqueRepository.findById(id)
                .orElseThrow(()-> new TechniqueNotFoundException(MessageUtils.TECHNIQUE_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public MessageResponseDTO createTechnique(TechniqueDTO techniqueDTO) {
        Optional<Technique> existTechnique = techniqueRepository.findById(techniqueDTO.getId());
        if(existTechnique.isPresent())
            throw new IllegalArgumentException(MessageUtils.TECHNIQUE_ALREADY_EXISTS.getDescription());
        try {
            Technique technique = techniqueMapper.toModel(techniqueDTO);
            techniqueRepository.save(technique);
            return createMessageResponse(technique.getId(), MessageUtils.TECHNIQUE_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.TECHNIQUE_SAVE_ERROR.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    public MessageResponseDTO updateTechnique(TechniqueDTO techniqueDTO) throws TechniqueNotFoundException {
        verifyIfExists(techniqueDTO.getId());
        try {
            Technique technique = techniqueMapper.toModel(techniqueDTO);
            techniqueRepository.save(technique);
            return createMessageResponse(technique.getId(), MessageUtils.TECHNIQUE_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.TECHNIQUE_UPDATE_ERROR.getDescription());
        }
    }


    public void deleteById(String id) throws TechniqueNotFoundException {
        verifyIfExists(id);
        try{
            techniqueRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.TECHNIQUE_DELETE_ERROR.getDescription());
        }
    }
}
