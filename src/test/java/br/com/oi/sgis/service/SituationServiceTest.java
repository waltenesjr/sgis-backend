package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.SituationDTO;
import br.com.oi.sgis.entity.Situation;
import br.com.oi.sgis.enums.ReasonForWriteOffEnum;
import br.com.oi.sgis.enums.SituationEnum;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.exception.SituationNotFoundException;
import br.com.oi.sgis.repository.SituationRepository;
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
class SituationServiceTest {

    @InjectMocks
    private SituationService situationService;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private SituationRepository situationRepository;

    @Test
    void listAllCrp(){
        List<Situation> situations = List.of(Situation.builder().id(SituationEnum.DEF.getCod()).build(),
                Situation.builder().id(SituationEnum.DIS.getCod()).build());
        Mockito.doReturn(situations).when(situationRepository).findCRPSituations(Mockito.anyList());

        List<SituationDTO> situationsToReturn = situationService.listAllCRP();

        Assertions.assertEquals(situations.size(), situationsToReturn.size());
        Assertions.assertEquals(situations.get(0).getId(), situationsToReturn.get(0).getId());
    }

    @Test
    void shouldSelectSituationDISForCUS() throws DepartmentNotFoundException {
        DepartmentDTO departmentDTO = new EasyRandom().nextObject(DepartmentDTO.class);
        departmentDTO.setId("RJ-OI-ARC");
        Situation situationDIS = Situation.builder().id(SituationEnum.DIS.getCod()).build();

        Mockito.doReturn(departmentDTO).when(departmentService).findById(Mockito.any());
        Mockito.doReturn(Optional.of(situationDIS)).when(situationRepository).findById(SituationEnum.DIS.getCod());

        SituationDTO situationReturned = situationService.selectSituationCUS("RJ-OI-ARC");
        Assertions.assertEquals(situationDIS.getId(), situationReturned.getId());
    }

    @Test
    void shouldSelectSituationDISForTRN() throws DepartmentNotFoundException {
        DepartmentDTO departmentDTO = new EasyRandom().nextObject(DepartmentDTO.class);
        Situation situationTRN = Situation.builder().id(SituationEnum.TRN.getCod()).build();

        Mockito.doReturn(departmentDTO).when(departmentService).findById(Mockito.any());
        Mockito.doReturn(Optional.of(situationTRN)).when(situationRepository).findById(SituationEnum.TRN.getCod());

        SituationDTO situationReturned = situationService.selectSituationCUS("RJ-OUTRO");
        Assertions.assertEquals(situationTRN.getId(), situationReturned.getId());
    }

    @Test
    void shouldSelectSituationWithException() throws DepartmentNotFoundException {
        Mockito.doThrow(DepartmentNotFoundException.class).when(departmentService).findById(Mockito.any());

        Assertions.assertThrows(DepartmentNotFoundException.class, () -> situationService.selectSituationCUS("RJ-OUTRO"));
    }

    @Test
    void findById() throws SituationNotFoundException {
        Situation situation = new EasyRandom().nextObject(Situation.class);
        Mockito.doReturn(Optional.of(situation)).when(situationRepository).findById(Mockito.anyString());

        SituationDTO situationDTO = situationService.findById(situation.getId());

        Assertions.assertEquals(situation.getId(), situationDTO.getId());
    }

    @Test
    void listAll(){
        List<Situation> situations = new EasyRandom().objects(Situation.class, 5).collect(Collectors.toList());
        Mockito.doReturn(situations).when(situationRepository).findAll();

        List<SituationDTO> situationsReturn = situationService.listAll();
        Assertions.assertEquals(situations.size(), situationsReturn.size());
    }
    @Test
    void listAllToUpdateUnity(){
        List<Situation> situations = new EasyRandom().objects(Situation.class, 5).collect(Collectors.toList());
        Mockito.doReturn(situations).when(situationRepository).findCRPSituations(Mockito.anyList());

        List<SituationDTO> situationsToReturn = situationService.listAllToUpdateUnity();

        Assertions.assertEquals(situations.size(), situationsToReturn.size());
        Assertions.assertEquals(situations.get(0).getId(), situationsToReturn.get(0).getId());
    }

    @Test
    void situationsByUnityWriteOffReason() {
        Mockito.doReturn(Optional.of(Situation.builder().id("TRC").build())).when(situationRepository).findById(Mockito.any());
        situationService.situationsByUnityWriteOffReason(ReasonForWriteOffEnum.OBS);
        Mockito.verify(situationRepository, Mockito.times(1)).findById(ReasonForWriteOffEnum.OBS.getSituation());
    }

    @Test
    void listAllPaginated(){
        List<Situation> situations = new EasyRandom().objects(Situation.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Situation> pagedResult = new PageImpl<>(situations, paging, situations.size());

        Mockito.doReturn(pagedResult).when(situationRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<SituationDTO> situationsToReturn = situationService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(situations.size(), situationsToReturn.getData().size());
    }

    @Test
    void shouldListAllSituationsWithoutTerm(){
        List<Situation> situations = new EasyRandom().objects(Situation.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Situation> pagedResult = new PageImpl<>(situations, paging, situations.size());

        Mockito.doReturn(pagedResult).when(situationRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<SituationDTO> situationsToReturn = situationService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(situations.size(), situationsToReturn.getData().size());
    }

    @Test
    void conciliate() throws SituationNotFoundException {
        Situation situation = new EasyRandom().nextObject(Situation.class);
        SituationDTO situationDTO = SituationDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(situation)).when(situationRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = situationService.conciliate(situationDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test
    void conciliateException() {
        Situation situation = new EasyRandom().nextObject(Situation.class);
        SituationDTO situationDTO = SituationDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(situation)).when(situationRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(situationRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> situationService.conciliate(situationDTO));
        assertEquals(MessageUtils.SITUATION_CONCILIATE_ERROR.getDescription(), e.getMessage());
    }

}