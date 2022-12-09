package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.ModelDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Model;
import br.com.oi.sgis.entity.ModelId;
import br.com.oi.sgis.exception.ModelNotFoundException;
import br.com.oi.sgis.mapper.ModelMapper;
import br.com.oi.sgis.repository.ModelRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ModelService {


    private final ModelRepository modelRepository;
    private static final ModelMapper modelMapper = ModelMapper.INSTANCE;

    public PaginateResponseDTO<Object> listAllPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap =  ModelMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, sortMap));
        if(term.isBlank())
            return PageableUtil.paginate(modelRepository.findAll(paging).map(modelMapper::toDTO), sortMap);

        return PageableUtil.paginate( modelRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(modelMapper::toDTO), sortMap);
    }

    @SneakyThrows
    public ModelDTO findById(ModelDTO dto)  throws ModelNotFoundException {
        Model modelToVerify = modelMapper.toModel(dto);
        Model model = verifyIfExists(modelToVerify.getId());
        return modelMapper.toDTO(model);
    }

    private Model verifyIfExists(ModelId modelId) throws ModelNotFoundException {
        return modelRepository.findById(modelId.getManufacturerCod().getId(), modelId.getModelCod())
                .orElseThrow(()-> new ModelNotFoundException(MessageUtils.MODEL_NOT_FOUND_BY_ID.getDescription() + modelId.getModelCod()));
    }

}
