package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.SituationDTO;
import br.com.oi.sgis.enums.ReasonForWriteOffEnum;
import br.com.oi.sgis.enums.SituationEnum;
import br.com.oi.sgis.exception.SituationNotFoundException;
import br.com.oi.sgis.service.SituationService;
import br.com.oi.sgis.util.PageableUtil;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class SituationControllerTest {

    @InjectMocks
    private SituationController situationController;

    @Mock
    private SituationService situationService;

    @Test
    void shouldListAllCRPSituations(){
        List<SituationDTO> situations = List.of(SituationDTO.builder().id(SituationEnum.DEF.getCod()).build(),
                SituationDTO.builder().id(SituationEnum.DIS.getCod()).build());
        Mockito.doReturn(situations).when(situationService).listAllCRP();

        ResponseEntity<List<SituationDTO>> situationsToReturn = situationController.listAllCRP();

        Assertions.assertNotNull(situationsToReturn.getBody());
        Assertions.assertEquals(situations.size(), situationsToReturn.getBody().size());
        Assertions.assertEquals(situations.get(0).getId(), situationsToReturn.getBody().get(0).getId());
        Assertions.assertEquals(HttpStatus.OK, situationsToReturn.getStatusCode());
    }

    @Test
    void shouldSelectSituationCUS(){
        SituationDTO situationDTO = SituationDTO.builder().id(SituationEnum.DIS.getCod()).build();
        Mockito.doReturn(situationDTO).when(situationService).selectSituationCUS(Mockito.anyString());

        SituationDTO situationReturned = situationController.selectSituationCus("A");
        Assertions.assertNotNull(situationReturned);
        Assertions.assertEquals(situationDTO.getId(), situationReturned.getId());
    }

    @Test
    void shouldFindSituationById() throws SituationNotFoundException{
        SituationDTO situationDTO = new EasyRandom().nextObject(SituationDTO.class);
        Mockito.doReturn(situationDTO).when(situationService).findById(Mockito.any());

        SituationDTO unityDtoResponse = situationController.findById("DIS");

        Assertions.assertEquals(situationDTO.getId(), unityDtoResponse.getId());
    }

    @Test
    void shouldListAllSituationsToUpdateUnity(){
        List<SituationDTO> situations = new EasyRandom().objects(SituationDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(situations).when(situationService).listAllToUpdateUnity();

        ResponseEntity<List<SituationDTO>> situationsToReturn = situationController.listAllSituationsForUpdateUnity();

        Assertions.assertNotNull(situationsToReturn.getBody());
        Assertions.assertEquals(situations.size(), situationsToReturn.getBody().size());
        Assertions.assertEquals(situations.get(0).getId(), situationsToReturn.getBody().get(0).getId());
        Assertions.assertEquals(HttpStatus.OK, situationsToReturn.getStatusCode());
    }

    @Test
    void shouldListAll(){
        List<SituationDTO> situations = new EasyRandom().objects(SituationDTO.class, 5).collect(Collectors.toList());
        Mockito.doReturn(situations).when(situationService).listAll();

        List<SituationDTO> situationsToReturn = situationController.listAll();

        Assertions.assertNotNull(situationsToReturn);
        Assertions.assertEquals(situations.size(), situationsToReturn.size());
        Assertions.assertEquals(situations.get(0).getId(), situationsToReturn.get(0).getId());
    }

    @Test
    void situationsByUnityWriteOffReason() {
        SituationDTO situation = SituationDTO.builder().id("TRC").build();
        Mockito.doReturn(situation).when(situationService).situationsByUnityWriteOffReason(Mockito.any());
        SituationDTO returnedSituation = situationController.situationsByUnityWriteOffReason(ReasonForWriteOffEnum.OBS);

        Assertions.assertNotNull(returnedSituation);
        Assertions.assertEquals("TRC", returnedSituation.getId());
    }

    @Test
    void listAllWithSearch() {
        List<SituationDTO> situationDTOS = new EasyRandom().objects(SituationDTO.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("description"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(situationDTOS, paging, situationDTOS.size()));

        Mockito.doReturn(expectedResponse).when(situationService).listAllPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<SituationDTO>> response = situationController.listAllWithSearch(0, 10,  List.of("id"), List.of("description"), "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void conciliate() throws SituationNotFoundException {
        SituationDTO situationDTO = new EasyRandom().nextObject(SituationDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(situationService).conciliate(Mockito.any());
        MessageResponseDTO returnedResponse = situationController.conciliate(situationDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }
}