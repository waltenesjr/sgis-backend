package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.BoxTypeDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.BoxType;
import br.com.oi.sgis.exception.BoxTypeNotFoundException;
import br.com.oi.sgis.repository.BoxTypeRepository;
import br.com.oi.sgis.util.MessageUtils;
import net.sf.jasperreports.engine.JRException;
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

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BoxTypeServiceTest {
    @InjectMocks
    private BoxTypeService boxTypeService;

    @Mock
    private BoxTypeRepository boxTypeRepository;
    @Mock
    private ReportService reportService;

    @Test
    void listAllPaginated(){
        List<BoxType> boxTypes = new EasyRandom().objects(BoxType.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<BoxType> pagedResult = new PageImpl<>(boxTypes, paging, boxTypes.size());

        Mockito.doReturn(pagedResult).when(boxTypeRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<BoxTypeDTO> boxTypesToReturn = boxTypeService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "RJ-");
        assertEquals(boxTypes.size(), boxTypesToReturn.getData().size());
    }

    @Test
    void shouldListAllBoxTypesWithoutTerm(){
        List<BoxType> boxTypes = new EasyRandom().objects(BoxType.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<BoxType> pagedResult = new PageImpl<>(boxTypes, paging, boxTypes.size());

        Mockito.doReturn(pagedResult).when(boxTypeRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<BoxTypeDTO> boxTypesToReturn = boxTypeService.listAllPaginated(0, 10, List.of("id"), List.of("date"), "");
        assertEquals(boxTypes.size(), boxTypesToReturn.getData().size());
    }

    @Test
    void findById() throws BoxTypeNotFoundException {
        BoxType boxType = new EasyRandom().nextObject(BoxType.class);
        Mockito.doReturn(Optional.of(boxType)).when(boxTypeRepository).findById(Mockito.anyString());

        BoxTypeDTO boxTypeDTO = boxTypeService.findById(boxType.getId());

        assertEquals(boxType.getId(), boxTypeDTO.getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        BoxType boxType = new EasyRandom().nextObject(BoxType.class);
        Mockito.doReturn(Optional.empty()).when(boxTypeRepository).findById(Mockito.anyString());
        Assertions.assertThrows(BoxTypeNotFoundException.class, () -> boxTypeService.findById(boxType.getId()));
    }

    @Test
    void createBoxType() {
        BoxTypeDTO boxTypeDTO = BoxTypeDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(boxTypeRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = boxTypeService.createBoxType(boxTypeDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createBoxTypeExistsException() {
        BoxType boxType = new EasyRandom().nextObject(BoxType.class);
        BoxTypeDTO boxTypeDTO = BoxTypeDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(boxType)).when(boxTypeRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> boxTypeService.createBoxType(boxTypeDTO));
        assertEquals(MessageUtils.BOX_TYPE_ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createBoxTypeException() {
        BoxTypeDTO boxTypeDTO = BoxTypeDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(boxTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(boxTypeRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> boxTypeService.createBoxType(boxTypeDTO));
        assertEquals(MessageUtils.BOX_TYPE_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateBoxType() throws BoxTypeNotFoundException {
        BoxType boxType = new EasyRandom().nextObject(BoxType.class);
        BoxTypeDTO boxTypeDTO = BoxTypeDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(boxType)).when(boxTypeRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = boxTypeService.updateBoxType(boxTypeDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test
    void updateBoxTypeException(){
        BoxType boxType = new EasyRandom().nextObject(BoxType.class);
        BoxTypeDTO boxTypeDTO = BoxTypeDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(boxType)).when(boxTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(boxTypeRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> boxTypeService.updateBoxType(boxTypeDTO));
        assertEquals(MessageUtils.BOX_TYPE_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void boxTypeReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        BoxType boxType = BoxType.builder().id("1").description("Teste").build();
        List<BoxType> boxTypes = List.of(boxType, boxType);
        Pageable paging = PageRequest.of(0, 10);
        Page<BoxType> pagedResult = new PageImpl<>(boxTypes, paging, boxTypes.size());
        Mockito.doReturn(pagedResult).when(boxTypeRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = boxTypeService.boxTypeReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void boxTypeReportEmpty(){
        List<BoxType> boxTypes = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<BoxType> pagedResult = new PageImpl<>(boxTypes, paging, boxTypes.size());
        Mockito.doReturn(pagedResult).when(boxTypeRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> boxTypeService.boxTypeReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws BoxTypeNotFoundException {
        BoxType boxType = new EasyRandom().nextObject(BoxType.class);
        Mockito.doReturn(Optional.of(boxType)).when(boxTypeRepository).findById(Mockito.any());
        boxTypeService.deleteById(boxType.getId());
        Mockito.verify(boxTypeRepository, Mockito.times(1)).deleteById(boxType.getId());
    }

    @Test
    void deleteByIdException()  {
        BoxType boxType = new EasyRandom().nextObject(BoxType.class);
        Mockito.doReturn(Optional.of(boxType)).when(boxTypeRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(boxTypeRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> boxTypeService.deleteById("1"));
        assertEquals(MessageUtils.BOX_TYPE_DELETE_ERROR.getDescription(), e.getMessage());
    }
}
