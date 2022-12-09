package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.AreaEquipamentDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.ReportCrudSearchDTO;
import br.com.oi.sgis.entity.AreaEquipament;
import br.com.oi.sgis.enums.ActiveClassEnum;
import br.com.oi.sgis.exception.AreaEquipamentNotFoundException;
import br.com.oi.sgis.service.AreaEquipamentService;
import br.com.oi.sgis.util.ExceptionHandlerAdvice;
import br.com.oi.sgis.util.PageableUtil;
import net.sf.jasperreports.engine.JRException;
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
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = ExceptionHandlerAdvice.class)
class AreaEquipamentControllerTest {

    @InjectMocks
    private AreaEquipamentController areaEquipamentController;

    @Mock
    private AreaEquipamentService areaEquipamentService;

    @Test
    void findById() throws AreaEquipamentNotFoundException {
        AreaEquipamentDTO areaEquipamentDTO = new EasyRandom().nextObject(AreaEquipamentDTO.class);
        Mockito.doReturn(areaEquipamentDTO).when(areaEquipamentService).findById(Mockito.any());

        AreaEquipamentDTO unityDtoResponse = areaEquipamentController.findById("12345");

        Assertions.assertEquals(areaEquipamentDTO.getId(), unityDtoResponse.getId());
    }

    @Test
    void listAllWithSearch(){
        List<AreaEquipament> areaEquipaments = new EasyRandom().objects(AreaEquipament.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(areaEquipaments, paging, areaEquipaments.size()));

        Mockito.doReturn(expectedResponse).when(areaEquipamentService).searchByTermsPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<AreaEquipamentDTO>> response = areaEquipamentController.listAllWithSearch(0, 10,  List.of("id"), List.of("registerDate"), "", LocalDateTime.now());

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void getActiveClass() throws AreaEquipamentNotFoundException {
        Mockito.doReturn(ActiveClassEnum.ZETSOBRE).when(areaEquipamentService).getActiveClass(Mockito.any());
        ResponseEntity<ActiveClassEnum> activeClass = areaEquipamentController.getActiveClass("S105");

        Assertions.assertNotNull(activeClass);
        Assertions.assertEquals(ActiveClassEnum.ZETSOBRE, activeClass.getBody());
    }

    @Test
    void listAllMnemonicsWithSearch(){
        List<Object> mnemonics = new EasyRandom().objects(Object.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.desc("mnemonic"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(mnemonics, paging, mnemonics.size()));

        Mockito.doReturn(expectedResponse).when(areaEquipamentService).searchMnemonics(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<Object>> response = areaEquipamentController.listAllMnemonicsWithSearch(0, 10,  List.of("id"),null, "");

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void createAreaEquipament() {
        AreaEquipamentDTO areaEquipamentDTO = new EasyRandom().nextObject(AreaEquipamentDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Mockito.doReturn(responseDTO).when(areaEquipamentService).createAreaEquipament(Mockito.any());
        MessageResponseDTO returnedResponse = areaEquipamentController.createAreaEquipament(areaEquipamentDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void updateAreaEquipament() throws AreaEquipamentNotFoundException {
        AreaEquipamentDTO areaEquipamentDTO = new EasyRandom().nextObject(AreaEquipamentDTO.class);
        MessageResponseDTO responseDTO = MessageResponseDTO.builder().status(HttpStatus.OK).build();
        Mockito.doReturn(responseDTO).when(areaEquipamentService).updateAreaEquipament(Mockito.any());
        MessageResponseDTO returnedResponse = areaEquipamentController.updateAreaEquipament(areaEquipamentDTO);
        assertEquals(responseDTO.getStatus(), returnedResponse.getStatus());
    }

    @Test
    void deleteById() throws AreaEquipamentNotFoundException {
        areaEquipamentController.deleteById("1");
        Mockito.verify(areaEquipamentService, Mockito.times(1)).deleteById("1");
    }

    @Test
    void report() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(areaEquipamentService).areaEquipamentReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ReportCrudSearchDTO reportCrudSearchDTO = ReportCrudSearchDTO.builder().search("").sortDesc(List.of()).sortAsc(List.of()).build();
        ResponseEntity<byte[]> responseReport = areaEquipamentController.report(reportCrudSearchDTO, LocalDateTime.now());

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }
}