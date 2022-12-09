package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.ManufacturerDTO;
import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.entity.Manufacturer;
import br.com.oi.sgis.exception.ManufacturerNotFoundException;
import br.com.oi.sgis.repository.ManufacturerRepository;
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
class ManufacturerServiceTest {

    @InjectMocks
    private ManufacturerService manufacturerService;

    @Mock
    private ManufacturerRepository manufacturerRepository;
    @Mock
    private ReportService reportService;


    @Test
    void listAllPaginated(){
        List<Manufacturer> manufacturers = new EasyRandom().objects(Manufacturer.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Manufacturer> pagedResult = new PageImpl<>(manufacturers, paging, manufacturers.size());

        Mockito.doReturn(pagedResult).when(manufacturerRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<ManufacturerDTO> manufacturersReturn = manufacturerService.listAllPaginated(0, 10, List.of("id"), List.of(), "RJ-");
        Assertions.assertEquals(manufacturers.size(), manufacturersReturn.getData().size());
    }

    @Test
    void shouldListAllWithSearchWithoutTerm(){
        List<Manufacturer> manufacturers = new EasyRandom().objects(Manufacturer.class, 5).collect(Collectors.toList());

        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Manufacturer> pagedResult = new PageImpl<>(manufacturers, paging, manufacturers.size());

        Mockito.doReturn(pagedResult).when(manufacturerRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<ManufacturerDTO> manufacturersReturn = manufacturerService.listAllPaginated(0, 10, List.of("id"), List.of(), "");
        Assertions.assertEquals(manufacturers.size(), manufacturersReturn.getData().size());
    }

    @Test
    void findById() throws ManufacturerNotFoundException {
        Manufacturer manufacturer = new EasyRandom().nextObject(Manufacturer.class);
        Mockito.doReturn(Optional.of(manufacturer)).when(manufacturerRepository).findById(Mockito.anyString());

        ManufacturerDTO manufacturerDTO = manufacturerService.findById(manufacturer.getId());

        assertEquals(manufacturer.getId(), manufacturerDTO.getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        Manufacturer manufacturer = new EasyRandom().nextObject(Manufacturer.class);
        Mockito.doReturn(Optional.empty()).when(manufacturerRepository).findById(Mockito.anyString());
        Assertions.assertThrows(ManufacturerNotFoundException.class, () -> manufacturerService.findById(manufacturer.getId()));
    }

    @Test
    void createManufacturer() {
        ManufacturerDTO manufacturerDTO = ManufacturerDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(manufacturerRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = manufacturerService.createManufacturer(manufacturerDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    void createManufacturerExistsException() {
        Manufacturer manufacturer = new EasyRandom().nextObject(Manufacturer.class);
        ManufacturerDTO manufacturerDTO = ManufacturerDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(manufacturer)).when(manufacturerRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> manufacturerService.createManufacturer(manufacturerDTO));
        assertEquals(MessageUtils.ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void createManufacturerException() {
        ManufacturerDTO manufacturerDTO = ManufacturerDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.empty()).when(manufacturerRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(manufacturerRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> manufacturerService.createManufacturer(manufacturerDTO));
        assertEquals(MessageUtils.MANUFACTURER_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateManufacturer() throws ManufacturerNotFoundException {
        Manufacturer manufacturer = new EasyRandom().nextObject(Manufacturer.class);
        ManufacturerDTO manufacturerDTO = ManufacturerDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(manufacturer)).when(manufacturerRepository).findById(Mockito.any());
        MessageResponseDTO responseDTO = manufacturerService.updateManufacturer(manufacturerDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

    }

    @Test
    void updateManufacturerException(){
        Manufacturer manufacturer = new EasyRandom().nextObject(Manufacturer.class);
        ManufacturerDTO manufacturerDTO = ManufacturerDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(manufacturer)).when(manufacturerRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(manufacturerRepository).save(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> manufacturerService.updateManufacturer(manufacturerDTO));
        assertEquals(MessageUtils.MANUFACTURER_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void manufacturerReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReport(Mockito.any(), Mockito.any());
        Manufacturer manufacturer = Manufacturer.builder().id("1").description("Teste").build();
        List<Manufacturer> manufacturers = List.of(manufacturer, manufacturer);
        Pageable paging = PageRequest.of(0, 10);
        Page<Manufacturer> pagedResult = new PageImpl<>(manufacturers, paging, manufacturers.size());
        Mockito.doReturn(pagedResult).when(manufacturerRepository).findAll((Pageable) Mockito.any());
        byte[] returnedReport = manufacturerService.manufacturerReport("", List.of(), List.of());
        assertNotNull(returnedReport);
    }

    @Test
    void manufacturerReportEmpty(){
        List<Manufacturer> manufacturers = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<Manufacturer> pagedResult = new PageImpl<>(manufacturers, paging, manufacturers.size());
        Mockito.doReturn(pagedResult).when(manufacturerRepository).findAll((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> manufacturerService.manufacturerReport("",null, null ));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void deleteById() throws ManufacturerNotFoundException {
        Manufacturer manufacturer = new EasyRandom().nextObject(Manufacturer.class);
        Mockito.doReturn(Optional.of(manufacturer)).when(manufacturerRepository).findById(Mockito.any());
        manufacturerService.deleteById(manufacturer.getId());
        Mockito.verify(manufacturerRepository, Mockito.times(1)).deleteById(manufacturer.getId());
    }

    @Test
    void deleteByIdException()  {
        Manufacturer manufacturer = new EasyRandom().nextObject(Manufacturer.class);
        Mockito.doReturn(Optional.of(manufacturer)).when(manufacturerRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(manufacturerRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> manufacturerService.deleteById("1"));
        assertEquals(MessageUtils.MANUFACTURER_DELETE_ERROR.getDescription(), e.getMessage());
    }
}