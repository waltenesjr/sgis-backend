package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.CentralDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.StationDTO;
import br.com.oi.sgis.entity.Central;
import br.com.oi.sgis.exception.CentralNotFoundException;
import br.com.oi.sgis.exception.StationNotFoundException;
import br.com.oi.sgis.repository.CentralRepository;
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
class CentralServiceTest {

    @InjectMocks
    private CentralService centralService;
    @Mock
    private ReportService reportService;
    @Mock
    private CentralRepository centralRepository;
    @Mock
    private StationService stationService;

    @Test
    void listAllPaginated() {
        List<Central> centrals = new EasyRandom().objects(Central.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Central> pagedResult = new PageImpl<>(centrals, paging, centrals.size());

        Mockito.doReturn(pagedResult).when(centralRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<CentralDTO> centralDTOSToReturn = centralService.listAllPaginated(0, 10, List.of("id"), List.of("description"), "ABC");
        Assertions.assertEquals(centrals.size(), centralDTOSToReturn.getData().size());
    }

    @Test
    void listAllPaginatedWithoutTerm() {
        List<Central> centrals = new EasyRandom().objects(Central.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Central> pagedResult = new PageImpl<>(centrals, paging, centrals.size());

        Mockito.doReturn(pagedResult).when(centralRepository).findAll( Mockito.any(Pageable.class));
        PaginateResponseDTO<CentralDTO> centralDTOSToReturn = centralService.listAllPaginated(0, 10, List.of("id"), List.of("description"), "");
        Assertions.assertEquals(centrals.size(), centralDTOSToReturn.getData().size());
    }

    @Test
    void findById() throws CentralNotFoundException {
        Central central = new EasyRandom().nextObject(Central.class);
        Mockito.doReturn(Optional.of(central)).when(centralRepository).findById(Mockito.any());
        CentralDTO centralDTO = centralService.findById("centralId");
        assertEquals(central.getId(), centralDTO.getId());
    }
    @Test
    void findByIdException() {
        Mockito.doReturn(Optional.empty()).when(centralRepository).findById(Mockito.any());
        Exception e = assertThrows(CentralNotFoundException.class, () -> centralService.findById("centralId"));
        assertEquals(MessageUtils.CENTRAL_NOT_FOUND_BY_ID.getDescription() + "centralId", e.getMessage());
    }

    @Test
    void createCentral() throws StationNotFoundException {
        CentralDTO centralDTO = new EasyRandom().nextObject(CentralDTO.class);
        Mockito.doReturn(Optional.empty()).when(centralRepository).findById(Mockito.any());
        Mockito.doReturn(new StationDTO()).when(stationService).findById(Mockito.any());
        MessageResponseDTO responseDTO = centralService.createCentral(centralDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createCentralExistsException() {
        CentralDTO centralDTO = new EasyRandom().nextObject(CentralDTO.class);
        Mockito.doReturn(Optional.of(new Central())).when(centralRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> centralService.createCentral(centralDTO));
        assertEquals(MessageUtils.ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createCentralException() throws StationNotFoundException {
        CentralDTO centralDTO = new EasyRandom().nextObject(CentralDTO.class);
        Mockito.doReturn(Optional.empty()).when(centralRepository).findById(Mockito.any());
        Mockito.doReturn(new StationDTO()).when(stationService).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(centralRepository).save(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> centralService.createCentral(centralDTO));
        assertEquals(MessageUtils.CENTRAL_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateCentral() throws StationNotFoundException, CentralNotFoundException {
        CentralDTO centralDTO = new EasyRandom().nextObject(CentralDTO.class);
        Mockito.doReturn(Optional.of(new Central())).when(centralRepository).findById(Mockito.any());
        Mockito.doReturn(new StationDTO()).when(stationService).findById(Mockito.any());
        MessageResponseDTO responseDTO = centralService.updateCentral(centralDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test
    void updateCentralException() throws StationNotFoundException {
        CentralDTO centralDTO = new EasyRandom().nextObject(CentralDTO.class);
        Mockito.doReturn(Optional.of(new Central())).when(centralRepository).findById(Mockito.any());
        Mockito.doReturn(new StationDTO()).when(stationService).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(centralRepository).save(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> centralService.updateCentral(centralDTO));
        assertEquals(MessageUtils.CENTRAL_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void centralReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        Central central = Central.builder().id("1").description("Teste").build();
        List<Central> centrals = List.of(central, central);
        Pageable paging = PageRequest.of(0, 10);
        Page<Central> pagedResult = new PageImpl<>(centrals, paging, centrals.size());
        Mockito.doReturn(pagedResult).when(centralRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = centralService.centralReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void centralReportEmpty(){
        List<Central> centrals = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<Central> pagedResult = new PageImpl<>(centrals, paging, centrals.size());
        Mockito.doReturn(pagedResult).when(centralRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> centralService.centralReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws CentralNotFoundException {
        Central central = new EasyRandom().nextObject(Central.class);
        Mockito.doReturn(Optional.of(central)).when(centralRepository).findById(Mockito.any());
        centralService.deleteById(central.getId());
        Mockito.verify(centralRepository, Mockito.times(1)).deleteById(central.getId());
    }

    @Test
    void deleteByIdException()  {
        Central central = new EasyRandom().nextObject(Central.class);
        Mockito.doReturn(Optional.of(central)).when(centralRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(centralRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> centralService.deleteById("1"));
        assertEquals(MessageUtils.CENTRAL_DELETE_ERROR.getDescription(), e.getMessage());
    }
}