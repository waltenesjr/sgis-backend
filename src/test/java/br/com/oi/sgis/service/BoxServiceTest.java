package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.Box;
import br.com.oi.sgis.entity.BoxType;
import br.com.oi.sgis.entity.Situation;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.exception.BoxNotFoundException;
import br.com.oi.sgis.repository.BoxRepository;
import br.com.oi.sgis.repository.BoxTypeRepository;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.util.MessageUtils;
import net.sf.jasperreports.engine.JRException;
import org.hibernate.tool.schema.ast.SqlScriptParserException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BoxServiceTest {

    @InjectMocks
    private BoxService boxService;

    @Mock
    private BoxRepository boxRepository;
    @Mock
    private BoxTypeRepository boxTypeRepository;
    @Mock
    private UnityRepository unityRepository;
    @Mock
    private ReportService reportService;

    @Test
    void listPaginated() {
        List<Box> boxes = new EasyRandom().objects(Box.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Box> pagedResult = new PageImpl<>(boxes, paging, boxes.size());

        Mockito.doReturn(pagedResult).when(boxRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> boxDTOSToReturn = boxService.listPaginated(0, 10, List.of("id"), List.of("description"), "term");
        assertEquals(boxes.size(), boxDTOSToReturn.getData().size());
    }

    @Test
    void listPaginatedNoneTerm() {
        List<Box> boxes = new EasyRandom().objects(Box.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Box> pagedResult = new PageImpl<>(boxes, paging, boxes.size());

        Mockito.doReturn(pagedResult).when(boxRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> boxDTOSToReturn = boxService.listPaginated(0, 10, List.of("id"), List.of("description"), "");
        assertEquals(boxes.size(), boxDTOSToReturn.getData().size());
    }

    @Test
    void findById() {
        Box box = new EasyRandom().nextObject(Box.class);
        Mockito.doReturn(Optional.of(box)).when(boxRepository).findById(Mockito.any());
        BoxDTO boxToReturn = boxService.findById("1L");
        assertEquals(box.getId(), boxToReturn.getId());
    }

    @Test
    void findByIdNoneFound() {
        Mockito.doReturn(Optional.empty()).when(boxRepository).findById(Mockito.any());
        Exception e = assertThrows(BoxNotFoundException.class, ()->boxService.findById("1L"));
        assertEquals(MessageUtils.BOX_NOT_FOUND_BY_ID.getDescription() + "1L", e.getMessage());
    }

    @Test
    void deleteById() {
        Box box = new EasyRandom().nextObject(Box.class);
        Mockito.doReturn(Optional.of(box)).when(boxRepository).findById(Mockito.anyString());
        boxService.deleteById(box.getId());
        Mockito.verify( boxRepository,Mockito.times(1)).findById(box.getId());
    }

    @Test
    void listBoxType() {
        BoxType boxType = BoxType.builder().id("1").description("descrição tipo").build();
        Mockito.doReturn(List.of(boxType, boxType)).when(boxTypeRepository).findAll();
        List<BoxTypeDTO> boxTypeDTOS = boxService.listBoxType();
        assertEquals(2, boxTypeDTOS.size());
        assertEquals(boxType.getId(), boxTypeDTOS.get(0).getId());
    }

    @Test
    void getBoxToUpdate() throws BoxNotFoundException {
        EasyRandom random = new EasyRandom();
        Unity unity = random.nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        Mockito.doReturn(List.of(unity)).when(unityRepository).findUnityByBox(Mockito.any());
        Box box = random.nextObject(Box.class);
        Mockito.doReturn(Optional.of(box)).when(boxRepository).findById(Mockito.any());

        BoxToUpdateDTO boxToUpdateDTO = boxService.getBoxToUpdate("1");
        assertEquals(box.getId(), boxToUpdateDTO.getBox().getId());
        assertEquals(1, boxToUpdateDTO.getUnities().size());
    }

    @Test
    void updateBox() throws BoxNotFoundException {
        EasyRandom random = new EasyRandom();
        BoxToUpdateDTO boxToUpdateDTO = random.nextObject(BoxToUpdateDTO.class);
        Box box = random.nextObject(Box.class);
        Mockito.doReturn(Optional.of(box)).when(boxRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = boxService.updateBox(boxToUpdateDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test
    void updateBoxError() {
        EasyRandom random = new EasyRandom();
        BoxToUpdateDTO boxToUpdateDTO = random.nextObject(BoxToUpdateDTO.class);
        Box box = random.nextObject(Box.class);
        Mockito.doReturn(Optional.of(box)).when(boxRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(boxRepository).updateBox(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, ()->boxService.updateBox(boxToUpdateDTO));
        assertEquals(MessageUtils.BOX_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void summaryBoxReport() throws JRException, IOException {
        EasyRandom random = new EasyRandom();
        Box box = random.nextObject(Box.class);
        Unity unity = random.nextObject(Unity.class);
        byte[] report = new byte[50];
        Mockito.doReturn(Optional.of(box)).when(boxRepository).findById(Mockito.any());
        Mockito.doReturn(report).when(reportService).summaryBoxReport(Mockito.any(), Mockito.any());
        Mockito.doReturn(List.of(unity)).when(unityRepository).findUnityByBox(Mockito.any());

        byte[] reurnedReport = boxService.summaryBoxReport("123");
        assertNotNull(reurnedReport);
    }

    @Test
    void summaryBoxReportException() throws JRException, IOException {
        EasyRandom random = new EasyRandom();
        Box box = random.nextObject(Box.class);
        Unity unity = random.nextObject(Unity.class);
        Mockito.doReturn(Optional.of(box)).when(boxRepository).findById(Mockito.any());
        Mockito.doThrow(JRException.class).when(reportService).summaryBoxReport(Mockito.any(), Mockito.any());
        Mockito.doReturn(List.of(unity)).when(unityRepository).findUnityByBox(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> boxService.summaryBoxReport("123"));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void summaryBoxReportExceptionItens() {
        EasyRandom random = new EasyRandom();
        Box box = random.nextObject(Box.class);
        Mockito.doReturn(Optional.of(box)).when(boxRepository).findById(Mockito.any());
        Mockito.doReturn(List.of()).when(unityRepository).findUnityByBox(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> boxService.summaryBoxReport("123"));
        assertEquals(MessageUtils.EMPTY_BOX.getDescription(), e.getMessage());
    }
}