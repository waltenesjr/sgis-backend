package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.CableMovement;
import br.com.oi.sgis.entity.ElectricalProperty;
import br.com.oi.sgis.exception.CableMovementNotFoundException;
import br.com.oi.sgis.mapper.CableMovementMapper;
import br.com.oi.sgis.repository.CableMovementRepository;
import br.com.oi.sgis.repository.ElectricalPropRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CableMovementServiceTest {
    @InjectMocks
    private CableMovementService cableMovementService;
    @Mock
    private CableMovementRepository cableMovementRepository;
    @Mock
    private ReportService reportService;
    @Mock
    private ElectricalPropRepository electricalPropRepository;

    @Test
    void listAllPaginated() {
        List<CableMovement> cableMovements = new EasyRandom().objects(CableMovement.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("sequence"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<CableMovement> pagedResult = new PageImpl<>(cableMovements, paging, cableMovements.size());
        CableMovementFilterDTO filterDTO = new EasyRandom().nextObject(CableMovementFilterDTO.class);

        Mockito.doReturn(pagedResult).when(cableMovementRepository).findLikeFilter( any(), any(Pageable.class));
        PaginateResponseDTO<CableMovementDTO> cableMovementDTOSToReturn = cableMovementService.listAllPaginated(0, 10, List.of("id"), List.of("description"), filterDTO);
        Assertions.assertEquals(cableMovements.size(), cableMovementDTOSToReturn.getData().size());
    }

    @Test
    void findById() throws CableMovementNotFoundException {
        CableMovement cableMovement = new EasyRandom().nextObject(CableMovement.class);
        Mockito.doReturn(Optional.of(cableMovement)).when(cableMovementRepository).findById(any(), any());
        CableMovementDTO cableMovementDTO = cableMovementService.findById(123L, "barcode");
        assertEquals(cableMovement.getId().getSequence(), cableMovementDTO.getSequence());
    }

    @Test
    void findByIdException() {
        Mockito.doReturn(Optional.empty()).when(cableMovementRepository).findById(any(), any());
        Exception e = assertThrows(CableMovementNotFoundException.class, () -> cableMovementService.findById(123L, "barcode"));
        assertEquals(MessageUtils.CABLE_MOV_NOT_FOUND_BY_ID.getDescription(), e.getMessage());
    }

    @Test
    void createCableMovement() {
        CableMovement cableMovement = new EasyRandom().nextObject(CableMovement.class);
        CableMovementMapper mapper = CableMovementMapper.INSTANCE;
        CableMovementDTO cableMovementDTO = mapper.toDTO(cableMovement);
        Mockito.doReturn(1L).when(cableMovementRepository).findLastId();
        MessageResponseDTO responseDTO = cableMovementService.createCableMovement(cableMovementDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createCableMovementException() {
        CableMovement cableMovement = new EasyRandom().nextObject(CableMovement.class);
        CableMovementMapper mapper = CableMovementMapper.INSTANCE;
        CableMovementDTO cableMovementDTO = mapper.toDTO(cableMovement);
        Mockito.doReturn(1L).when(cableMovementRepository).findLastId();
        Mockito.doThrow(SqlScriptParserException.class).when(cableMovementRepository).save(any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> cableMovementService.createCableMovement(cableMovementDTO));
        assertEquals(MessageUtils.CABLE_MOV_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void cableMovementReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).fillCableMovReport(Mockito.any(), Mockito.any());
        CableMovement cableMovement = new EasyRandom().nextObject(CableMovement.class);
        List<CableMovement> cableMovements = List.of(cableMovement, cableMovement);
        Pageable paging = PageRequest.of(0, 10);
        CableMovementFilterDTO filterDTO = new EasyRandom().nextObject(CableMovementFilterDTO.class);

        Page<CableMovement> pagedResult = new PageImpl<>(cableMovements, paging, cableMovements.size());
        Mockito.doReturn(pagedResult).when(cableMovementRepository).findLikeFilter( Mockito.any(),(Pageable) Mockito.any());
        byte[] returnedReport = cableMovementService.cableMovementReport(filterDTO, List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void cableMovementReportEmpty() {
        List<CableMovement> cableMovements = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<CableMovement> pagedResult = new PageImpl<>(cableMovements, paging, cableMovements.size());
        Mockito.doReturn(pagedResult).when(cableMovementRepository).findLikeFilter(Mockito.any(),(Pageable) Mockito.any());
        LocalDateTime dateTime = LocalDateTime.now();
        List<String> list = List.of();
        CableMovementFilterDTO filterDTO = new EasyRandom().nextObject(CableMovementFilterDTO.class);

        Exception e = assertThrows(IllegalArgumentException.class, () -> cableMovementService.cableMovementReport(filterDTO,  list, list));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void listCableMovementUnityProperties() {
        List<ElectricalProperty> electricalProperties = new EasyRandom().objects(ElectricalProperty.class, 5).collect(Collectors.toList());
        Mockito.doReturn(electricalProperties).when(electricalPropRepository).findElectricalPropertiesByUnity(Mockito.any());
        List<ElectricalPropDTO> properties = cableMovementService.listCableMovementUnityProperties("123");
        assertEquals(electricalProperties.size(), properties.size());
    }

    @Test
    void listCableMovementUnityPropertiesException() {
        Mockito.doReturn(List.of()).when(electricalPropRepository).findElectricalPropertiesByUnity(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> cableMovementService.listCableMovementUnityProperties("123"));
        assertEquals(MessageUtils.CABLE_MOV_PROP_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void getCableMovement() {
        List<CableMovement> cableMovements = new EasyRandom().objects(CableMovement.class, 5).collect(Collectors.toList());
        CableMovementQueryDTO queryDTO = new EasyRandom().nextObject(CableMovementQueryDTO.class);
        queryDTO.setFinalDate(null);
        queryDTO.setInitialDate(null);
        List<Sort.Order> orders = List.of(Sort.Order.asc("sequence"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<CableMovement> pagedResult = new PageImpl<>(cableMovements, paging, cableMovements.size());

        Mockito.doReturn(pagedResult).when(cableMovementRepository).findCableMovement(Mockito.any(), any(Pageable.class));
        PaginateResponseDTO<CableMovementDTO> cableMovementDTOSToReturn = cableMovementService.getCableMovement(queryDTO,0, 10, List.of("id"), List.of("description"));
        Assertions.assertEquals(cableMovements.size(), cableMovementDTOSToReturn.getData().size());
    }
    @Test
    void getCableMovementWithDate() {
        List<CableMovement> cableMovements = new EasyRandom().objects(CableMovement.class, 5).collect(Collectors.toList());
        CableMovementQueryDTO queryDTO = new EasyRandom().nextObject(CableMovementQueryDTO.class);
        queryDTO.setFinalDate(LocalDateTime.now().plusDays(4));
        queryDTO.setInitialDate(LocalDateTime.now());
        List<Sort.Order> orders = List.of(Sort.Order.asc("sequence"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<CableMovement> pagedResult = new PageImpl<>(cableMovements, paging, cableMovements.size());

        Mockito.doReturn(pagedResult).when(cableMovementRepository).findCableMovementWithDateFilter(Mockito.any(), any(Pageable.class));
        PaginateResponseDTO<CableMovementDTO> cableMovementDTOSToReturn = cableMovementService.getCableMovement(queryDTO,0, 10, List.of("id"), List.of("description"));
        Assertions.assertEquals(cableMovements.size(), cableMovementDTOSToReturn.getData().size());
    }

    @Test
    void cableMovementQueryReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).fillCableMovQueryReport(Mockito.any(), Mockito.any());
        CableMovement cableMovement = new EasyRandom().nextObject(CableMovement.class);
        CableMovementQueryDTO queryDTO = new EasyRandom().nextObject(CableMovementQueryDTO.class);
        queryDTO.setFinalDate(LocalDateTime.now().plusDays(4));
        queryDTO.setInitialDate(LocalDateTime.now());
        List<CableMovement> cableMovements = List.of(cableMovement, cableMovement);
        Pageable paging = PageRequest.of(0, 10);
        Page<CableMovement> pagedResult = new PageImpl<>(cableMovements, paging, cableMovements.size());
        Mockito.doReturn(pagedResult).when(cableMovementRepository).findCableMovementWithDateFilter(Mockito.any(), Mockito.any());
        byte[] returnedReport = cableMovementService.cableMovementQueryReport(queryDTO,  List.of("id"), List.of("description"));
        assertNotNull(returnedReport);
    }

    @Test
    void cableMovementQueryReportEmpty() {
        List<CableMovement> cableMovements = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<CableMovement> pagedResult = new PageImpl<>(cableMovements, paging, cableMovements.size());
        Mockito.doReturn(pagedResult).when(cableMovementRepository).findCableMovementWithDateFilter(Mockito.any(),(Pageable) Mockito.any());
        CableMovementQueryDTO queryDTO = new EasyRandom().nextObject(CableMovementQueryDTO.class);
        queryDTO.setFinalDate(LocalDateTime.now().plusDays(4));
        queryDTO.setInitialDate(LocalDateTime.now());
        Exception e = assertThrows(IllegalArgumentException.class, () -> cableMovementService.cableMovementQueryReport(queryDTO,  null, null));
    }
}