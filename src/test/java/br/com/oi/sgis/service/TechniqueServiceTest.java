package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.TechniqueDTO;
import br.com.oi.sgis.entity.Technique;
import br.com.oi.sgis.exception.TechniqueNotFoundException;
import br.com.oi.sgis.repository.TechniqueRepository;
import br.com.oi.sgis.util.MessageUtils;
import org.hibernate.tool.schema.ast.SqlScriptParserException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
@ExtendWith(MockitoExtension.class)
class TechniqueServiceTest {
    @InjectMocks
    private TechniqueService techniqueService;

    @Mock
    private TechniqueRepository techniqueRepository;

    @Test
    void listAllPaginated(){
        List<Technique> techniques = new EasyRandom().objects(Technique.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Technique> pagedResult = new PageImpl<>(techniques, paging, techniques.size());

        Mockito.doReturn(pagedResult).when(techniqueRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<TechniqueDTO> techniquesToReturn = techniqueService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(techniques.size(), techniquesToReturn.getData().size());
    }

    @Test
    void shouldListAllTechniquesWithoutTerm(){
        List<Technique> techniques = new EasyRandom().objects(Technique.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Technique> pagedResult = new PageImpl<>(techniques, paging, techniques.size());

        Mockito.doReturn(pagedResult).when(techniqueRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<TechniqueDTO> techniquesToReturn = techniqueService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(techniques.size(), techniquesToReturn.getData().size());
    }

    @Test
    void findById() throws TechniqueNotFoundException {
        Technique technique = new EasyRandom().nextObject(Technique.class);
        technique.setTechniqueType("FIXA");
        Mockito.doReturn(Optional.of(technique)).when(techniqueRepository).findById(Mockito.anyString());

        TechniqueDTO techniqueDTO = techniqueService.findById(technique.getId());

        assertEquals(technique.getId(), techniqueDTO.getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        Technique technique = new EasyRandom().nextObject(Technique.class);
        Mockito.doReturn(Optional.empty()).when(techniqueRepository).findById(Mockito.anyString());
        Assertions.assertThrows(TechniqueNotFoundException.class, () -> techniqueService.findById(technique.getId()));
    }

    @Test
    void createTechnique() {
        TechniqueDTO techniqueDTO = TechniqueDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(techniqueRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = techniqueService.createTechnique(techniqueDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createTechniqueExistsException() {
        Technique technique = new EasyRandom().nextObject(Technique.class);
        TechniqueDTO techniqueDTO = TechniqueDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(technique)).when(techniqueRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> techniqueService.createTechnique(techniqueDTO));
        assertEquals(MessageUtils.TECHNIQUE_ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createTechniqueException() {
        TechniqueDTO techniqueDTO = TechniqueDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(techniqueRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(techniqueRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> techniqueService.createTechnique(techniqueDTO));
        assertEquals(MessageUtils.TECHNIQUE_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateTechnique() throws TechniqueNotFoundException {
        Technique technique = new EasyRandom().nextObject(Technique.class);
        TechniqueDTO techniqueDTO = TechniqueDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(technique)).when(techniqueRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = techniqueService.updateTechnique(techniqueDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test
    void updateTechniqueException(){
        Technique technique = new EasyRandom().nextObject(Technique.class);
        TechniqueDTO techniqueDTO = TechniqueDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(technique)).when(techniqueRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(techniqueRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> techniqueService.updateTechnique(techniqueDTO));
        assertEquals(MessageUtils.TECHNIQUE_UPDATE_ERROR.getDescription(), e.getMessage());
    }


    @Test
    void deleteById() throws TechniqueNotFoundException {
        Technique technique = new EasyRandom().nextObject(Technique.class);
        Mockito.doReturn(Optional.of(technique)).when(techniqueRepository).findById(Mockito.any());
        techniqueService.deleteById(technique.getId());
        Mockito.verify(techniqueRepository, Mockito.times(1)).deleteById(technique.getId());
    }

    @Test
    void deleteByIdException()  {
        Technique technique = new EasyRandom().nextObject(Technique.class);
        Mockito.doReturn(Optional.of(technique)).when(techniqueRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(techniqueRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> techniqueService.deleteById("1"));
        assertEquals(MessageUtils.TECHNIQUE_DELETE_ERROR.getDescription(), e.getMessage());
    }
}
