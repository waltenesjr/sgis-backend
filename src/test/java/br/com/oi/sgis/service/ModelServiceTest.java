package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.ModelDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Model;
import br.com.oi.sgis.exception.ModelNotFoundException;
import br.com.oi.sgis.repository.ModelRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {

    @InjectMocks
    private ModelService modelService;

    @Mock
    private ModelRepository modelRepository;


    @Test
    void listAllPaginated(){
        List<Model> models = new EasyRandom().objects(Model.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Model> pagedResult = new PageImpl<>(models, paging, models.size());

        Mockito.doReturn(pagedResult).when(modelRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> modelsReturn = modelService.listAllPaginated(0, 10, List.of("id"), List.of(), "RJ-");
        Assertions.assertEquals(models.size(), modelsReturn.getData().size());
    }

    @Test
    void shouldListAllWithSearchWithoutTerm(){
        List<Model> models = new EasyRandom().objects(Model.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Model> pagedResult = new PageImpl<>(models, paging, models.size());

        Mockito.doReturn(pagedResult).when(modelRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> modelsReturn = modelService.listAllPaginated(0, 10, List.of("id"), List.of(), "");
        Assertions.assertEquals(models.size(), modelsReturn.getData().size());
    }

    @Test
    void findById() throws ModelNotFoundException {
        ModelDTO modelDTO = new EasyRandom().nextObject(ModelDTO.class);
        Model model = new EasyRandom().nextObject(Model.class);
        Mockito.doReturn(Optional.of(model)).when(modelRepository).findById(Mockito.any(), Mockito.any());
        ModelDTO modelToReturn = modelService.findById(modelDTO);
        Assertions.assertNotNull(modelToReturn);
        Assertions.assertEquals(model.getId().getModelCod(), modelToReturn.getModelCod());
    }

    @Test
    void shouldFindByIdWithException() {
        ModelDTO modelDTO = new EasyRandom().nextObject(ModelDTO.class);
        Mockito.doReturn(Optional.empty()).when(modelRepository).findById(Mockito.any(), Mockito.any());
        Assertions.assertThrows(ModelNotFoundException.class, () -> modelService.findById(modelDTO));
    }
}