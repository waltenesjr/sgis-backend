package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.AddressDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.StationDTO;
import br.com.oi.sgis.entity.Station;
import br.com.oi.sgis.entity.Uf;
import br.com.oi.sgis.exception.StationNotFoundException;
import br.com.oi.sgis.mapper.StationMapper;
import br.com.oi.sgis.repository.StationRepository;
import br.com.oi.sgis.repository.UfRepository;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {
    @InjectMocks
    private StationService stationService;

    @Mock
    private StationRepository stationRepository;
    @Mock
    private ReportService reportService;
    @Mock
    private AddressService addressService;
    @Mock
    private UfRepository ufRepository;
    @MockBean
    private StationMapper stationMapper = StationMapper.INSTANCE;

    @Test
    void listAll(){
        List<Station> stations = new EasyRandom().objects(Station.class, 5).collect(Collectors.toList());
        Mockito.doReturn(stations).when(stationRepository).findAll();

        List<StationDTO> stationDTOSToReturn = stationService.listAll();
        Assertions.assertEquals(stations.size(), stationDTOSToReturn.size());
        Mockito.verify(stationRepository, Mockito.times(1)).findAll();
    }

    @Test
    void listPaginated(){
        List<Station> stations = new EasyRandom().objects(Station.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Station> pagedResult = new PageImpl<>(stations, paging, stations.size());

        Mockito.doReturn(pagedResult).when(stationRepository).findLike(Mockito.anyString(),Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<StationDTO> stationDTOSToReturn = stationService.listPaginated(0, 10, List.of("id"), List.of("description"), "S123");
        Assertions.assertEquals(stations.size(), stationDTOSToReturn.getData().size());
    }

    @Test
    void shouldListAllWithSearchWithoutTerm(){
        List<Station> stations = new EasyRandom().objects(Station.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Station> pagedResult = new PageImpl<>(stations, paging, stations.size());

        Mockito.doReturn(pagedResult).when(stationRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<StationDTO> stationDTOSToReturn = stationService.listPaginated(0, 10, List.of("id"), List.of("description"), "");
        Assertions.assertEquals(stations.size(), stationDTOSToReturn.getData().size());
    }

    @Test
    void findById() throws StationNotFoundException {
        Station station = new EasyRandom().nextObject(Station.class);

        Mockito.doReturn(Optional.of(station)).when(stationRepository).findById(Mockito.any());
        StationDTO stationToReturn = stationService.findById("1L");

        Assertions.assertEquals(station.getId(), stationToReturn.getId());
    }
    @Test
    void shouldFindByIdWithException() {
        Mockito.doReturn(Optional.empty()).when(stationRepository).findById(Mockito.any());

        Assertions.assertThrows(StationNotFoundException.class, () -> stationService.findById("1L"));
    }

    @Test
    void createStation() {
        StationDTO stationDTO = new EasyRandom().nextObject(StationDTO.class);
        AddressDTO addressDTO = AddressDTO.builder().build();
        Uf uf = Uf.builder().build();
        Mockito.doReturn(Optional.empty()).when(stationRepository).findById(Mockito.any());
        Mockito.doReturn(addressDTO).when(addressService).findById(Mockito.any());
        Mockito.doReturn(Optional.of(uf)).when(ufRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = stationService.createStation(stationDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createStationExistsException() {
        Station station = new EasyRandom().nextObject(Station.class);
        StationDTO stationDTO = StationDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(station)).when(stationRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> stationService.createStation(stationDTO));
        assertEquals(MessageUtils.STATION_ALREADY_REGISTERED.getDescription(), e.getMessage());
    }

    @Test
    void createStationException() {
        StationDTO stationDTO = StationDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(stationRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(stationRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> stationService.createStation(stationDTO));
        assertEquals(MessageUtils.STATION_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateStation() throws StationNotFoundException {
        Station station = new EasyRandom().nextObject(Station.class);
        StationDTO stationDTO = StationDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(station)).when(stationRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = stationService.updateStation(stationDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test
    void updateStationException(){
        Station station = new EasyRandom().nextObject(Station.class);
        StationDTO stationDTO = StationDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(station)).when(stationRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(stationRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> stationService.updateStation(stationDTO));
        assertEquals(MessageUtils.STATION_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void stationReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).fillStationReport(Mockito.any(), Mockito.any());
        Station station = Station.builder().id("1").description("Teste").build();
        List<Station> stations = List.of(station, station);
        Pageable paging = PageRequest.of(0, 10);
        Page<Station> pagedResult = new PageImpl<>(stations, paging, stations.size());
        Mockito.doReturn(pagedResult).when(stationRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = stationService.stationReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void stationReportEmpty(){
        List<Station> stations = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<Station> pagedResult = new PageImpl<>(stations, paging, stations.size());
        Mockito.doReturn(pagedResult).when(stationRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> stationService.stationReport("", null, null));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws StationNotFoundException {
        Station station = new EasyRandom().nextObject(Station.class);
        Mockito.doReturn(Optional.of(station)).when(stationRepository).findById(Mockito.any());
        stationService.deleteById(station.getId());
        Mockito.verify(stationRepository, Mockito.times(1)).deleteById(station.getId());
    }

    @Test
    void deleteByIdException()  {
        Station station = new EasyRandom().nextObject(Station.class);
        Mockito.doReturn(Optional.of(station)).when(stationRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(stationRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> stationService.deleteById("1"));
        assertEquals(MessageUtils.STATION_DELETE_ERROR.getDescription(), e.getMessage());
    }
}