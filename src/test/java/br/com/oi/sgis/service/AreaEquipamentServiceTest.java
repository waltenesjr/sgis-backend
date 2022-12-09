package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.AreaEquipament;
import br.com.oi.sgis.enums.ActiveClassEnum;
import br.com.oi.sgis.enums.TechiniqueCodeEnum;
import br.com.oi.sgis.exception.AreaEquipamentNotFoundException;
import br.com.oi.sgis.mapper.AreaEquipamentMapper;
import br.com.oi.sgis.repository.AreaEquipamentRepository;
import br.com.oi.sgis.repository.ElectricalPropEquipRepository;
import br.com.oi.sgis.util.MessageUtils;
import lombok.SneakyThrows;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;


@ExtendWith(MockitoExtension.class)
class AreaEquipamentServiceTest {

    @InjectMocks
    private AreaEquipamentService areaEquipamentService;

    @Mock
    private AreaEquipamentRepository areaEquipamentRepository;

    @MockBean
    private AreaEquipamentMapper areaEquipamentMapper = AreaEquipamentMapper.INSTANCE;

    @Mock
    private ReportService reportService;
    @Mock
    private TechnicalStaffService technicalStaffService;
    @Mock
    private ModelEquipTypeService modelEquipTypeService;
    @Mock
    private ElectricalPropEquipRepository electricalPropEquipRepository;


    @Test
    void findById() throws AreaEquipamentNotFoundException {
        AreaEquipament areaEquipament = new EasyRandom().nextObject(AreaEquipament.class);
        Mockito.doReturn(Optional.of(areaEquipament)).when(areaEquipamentRepository).findById(anyString());

        AreaEquipamentDTO areaEquipamentDTO = areaEquipamentService.findById(areaEquipament.getId());

        Assertions.assertEquals(areaEquipament.getId(), areaEquipamentDTO.getId());
    }

    @Test
    void shouldDoThrowOnFindById() {
        AreaEquipament areaEquipament = new EasyRandom().nextObject(AreaEquipament.class);
        Mockito.doReturn(Optional.empty()).when(areaEquipamentRepository).findById(anyString());
        Assertions.assertThrows(AreaEquipamentNotFoundException.class, () -> areaEquipamentService.findById(areaEquipament.getId()));
    }

    @Test
    void deleteById() throws AreaEquipamentNotFoundException {
        AreaEquipament areaEquipament = new EasyRandom().nextObject(AreaEquipament.class);
        Mockito.doReturn(Optional.of(areaEquipament)).when(areaEquipamentRepository).findById(anyString());

        areaEquipamentService.deleteById(areaEquipament.getId());
        Mockito.verify(areaEquipamentRepository, Mockito.times(1)).findById(areaEquipament.getId());
    }

    @Test
    void deleteByIdException()  {
        AreaEquipament areaEquipament = new EasyRandom().nextObject(AreaEquipament.class);
        Mockito.doReturn(Optional.of(areaEquipament)).when(areaEquipamentRepository).findById(anyString());

        Mockito.doThrow(SqlScriptParserException.class).when(areaEquipamentRepository).deleteById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> areaEquipamentService.deleteById("1"));
        assertEquals(MessageUtils.AREA_EQUIPAMENT_DELETE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void shouldDeleteByIdWithException() {
        AreaEquipament areaEquipament = new EasyRandom().nextObject(AreaEquipament.class);
        Mockito.doReturn(Optional.empty()).when(areaEquipamentRepository).findById(anyString());

        Assertions.assertThrows(AreaEquipamentNotFoundException.class, () -> areaEquipamentService.deleteById(areaEquipament.getId()));
    }

    @Test
    void searchByTermsPaginated() {
        List<AreaEquipament> areaEquipaments = new EasyRandom().objects(AreaEquipament.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<AreaEquipament> pagedResult = new PageImpl<>(areaEquipaments, paging, areaEquipaments.size());

        Mockito.doReturn(pagedResult).when(areaEquipamentRepository).findLikeWithDate(anyString(), Mockito.any(), Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<AreaEquipamentDTO> areaEquipamensReturn = areaEquipamentService.searchByTermsPaginated(0, 10, List.of("id"), List.of("date"), "RJ-", LocalDateTime.now());
        Assertions.assertEquals(areaEquipaments.size(), areaEquipamensReturn.getData().size());
    }

    @Test
    void searchByTermsPaginatedOnlyTerm() {
        List<AreaEquipament> areaEquipaments = new EasyRandom().objects(AreaEquipament.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<AreaEquipament> pagedResult = new PageImpl<>(areaEquipaments, paging, areaEquipaments.size());

        Mockito.doReturn(pagedResult).when(areaEquipamentRepository).findLike(anyString(), Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<AreaEquipamentDTO> areaEquipamensReturn = areaEquipamentService.searchByTermsPaginated(0, 10, List.of("id"), List.of("date"), "RJ-", null);
        Assertions.assertEquals(areaEquipaments.size(), areaEquipamensReturn.getData().size());
    }

    @Test
    void shouldListAllWithSearchWithoutTerm() {
        List<AreaEquipament> areaEquipaments = new EasyRandom().objects(AreaEquipament.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("date"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<AreaEquipament> pagedResult = new PageImpl<>(areaEquipaments, paging, areaEquipaments.size());

        Mockito.doReturn(pagedResult).when(areaEquipamentRepository).findAllByDiscontinuedFlagFalse(Mockito.any(Pageable.class));
        PaginateResponseDTO<AreaEquipamentDTO> areaEquipamensReturn = areaEquipamentService.searchByTermsPaginated(0, 10, List.of("id"), List.of("date"), "", null);
        Assertions.assertEquals(areaEquipaments.size(), areaEquipamensReturn.getData().size());
    }

    @Test
    void shouldGetActiveClassZETSOBRE() throws AreaEquipamentNotFoundException {
        AreaEquipament areaEquipament = new EasyRandom().nextObject(AreaEquipament.class);
        Mockito.doReturn(Optional.of(areaEquipament)).when(areaEquipamentRepository).findById(Mockito.any());

        ActiveClassEnum acticveClass = areaEquipamentService.getActiveClass("S105");

        Assertions.assertEquals(ActiveClassEnum.ZETSOBRE, acticveClass);
    }

    @Test
    void getActiveClass() throws AreaEquipamentNotFoundException {
        AreaEquipament areaEquipament = new EasyRandom().nextObject(AreaEquipament.class);
        areaEquipament.setTechniqueCode(TechiniqueCodeEnum.INST.getCod());
        Mockito.doReturn(Optional.of(areaEquipament)).when(areaEquipamentRepository).findById(Mockito.any());

        ActiveClassEnum acticveClass = areaEquipamentService.getActiveClass("S105");

        Assertions.assertEquals(ActiveClassEnum.ZXFERINS, acticveClass);
    }

    @Test
    void shouldGetActiveClassNull() throws AreaEquipamentNotFoundException {
        AreaEquipament areaEquipament = new EasyRandom().nextObject(AreaEquipament.class);
        areaEquipament.setTechniqueCode(null);
        Mockito.doReturn(Optional.of(areaEquipament)).when(areaEquipamentRepository).findById(Mockito.any());

        ActiveClassEnum acticveClass = areaEquipamentService.getActiveClass("S105");

        Assertions.assertNull(acticveClass);
    }

    @Test
    void searchMnemonics() {
        List<Object> mnemonics = new EasyRandom().objects(String.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("menmonic"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Object> pagedResult = new PageImpl<>(mnemonics, paging, mnemonics.size());

        Mockito.doReturn(pagedResult).when(areaEquipamentRepository).findAllByDistinctMnemonic(anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> areaEquipamensReturn = areaEquipamentService.searchMnemonics(0, 10, List.of("id"), List.of("date"), "RJ-");
        Assertions.assertEquals(mnemonics.size(), areaEquipamensReturn.getData().size());
    }

    @Test
    void shouldListAllMnemonicsWithoutTerm() {
        List<Object> mnemonics = new EasyRandom().objects(String.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("menmonic"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Object> pagedResult = new PageImpl<>(mnemonics, paging, mnemonics.size());

        Mockito.doReturn(pagedResult).when(areaEquipamentRepository).findAllByDistinctMnemonic(Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> areaEquipamensReturn = areaEquipamentService.searchMnemonics(0, 10, List.of("menmonic"), null, "");
        Assertions.assertEquals(mnemonics.size(), areaEquipamensReturn.getData().size());
    }

    @Test
    void findByMnemonic() throws AreaEquipamentNotFoundException {
        AreaEquipament areaEquipament = AreaEquipament.builder().id("123").build();
        Mockito.doReturn(Optional.of(areaEquipament)).when(areaEquipamentRepository).findTopByMnemonicContains(anyString());
        AreaEquipament areaReturned = areaEquipamentService.findByMnemonic("123");
        Assertions.assertEquals(areaEquipament.getId(), areaReturned.getId());
    }

    @Test
    void shouldFindByMnemonicWithException() {
        Mockito.doReturn(Optional.empty()).when(areaEquipamentRepository).findTopByMnemonicContains(anyString());
        Exception e = Assertions.assertThrows(AreaEquipamentNotFoundException.class, () -> areaEquipamentService.findByMnemonic("123"));
        Assertions.assertEquals(MessageUtils.AREA_EQUIPAMENT_NOT_FOUND_BY_MNEMONIC.getDescription() + "123", e.getMessage());
    }

    @Test
    @SneakyThrows
    void createAreaEquipament() {
        AreaEquipamentDTO areaEquipamentDTO = new EasyRandom().nextObject(AreaEquipamentDTO.class);
        ModelEquipTypeDTO modelEquipTypeDTO = new EasyRandom().nextObject(ModelEquipTypeDTO.class);
        Mockito.doReturn(Optional.empty()).when(areaEquipamentRepository).findById(Mockito.any());
        Mockito.doReturn(modelEquipTypeDTO).when(modelEquipTypeService).findById(Mockito.any());
        Mockito.doReturn(new TechnicalStaffDTO()).when(technicalStaffService).findById(Mockito.any());
        MessageResponseDTO responseDTO = areaEquipamentService.createAreaEquipament(areaEquipamentDTO);
        assertEquals(HttpStatus.CREATED, responseDTO.getStatus());
    }

    @Test
    @SneakyThrows
    void createAreaEquipamentExceptionEletricProp() {
        AreaEquipamentDTO areaEquipamentDTO = new EasyRandom().nextObject(AreaEquipamentDTO.class);
        ModelEquipTypeDTO modelEquipTypeDTO = new EasyRandom().nextObject(ModelEquipTypeDTO.class);
        Mockito.doReturn(Optional.empty()).when(areaEquipamentRepository).findById(Mockito.any());
        Mockito.doReturn(modelEquipTypeDTO).when(modelEquipTypeService).findById(Mockito.any());
        Mockito.doReturn(new TechnicalStaffDTO()).when(technicalStaffService).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(electricalPropEquipRepository).saveAll(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> areaEquipamentService.createAreaEquipament(areaEquipamentDTO));
        assertEquals(MessageUtils.AREA_EQUIPAMENT_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void createAreaEquipamentExistsException() {
        AreaEquipament areaEquipament = new EasyRandom().nextObject(AreaEquipament.class);
        AreaEquipamentDTO areaEquipamentDTO = AreaEquipamentDTO.builder().id("1").description("Teste").build();
        Mockito.doReturn(Optional.of(areaEquipament)).when(areaEquipamentRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> areaEquipamentService.createAreaEquipament(areaEquipamentDTO));
        assertEquals(MessageUtils.ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    @SneakyThrows
    void createAreaEquipamentException() {
        AreaEquipamentDTO areaEquipamentDTO = new EasyRandom().nextObject(AreaEquipamentDTO.class);
        ModelEquipTypeDTO modelEquipTypeDTO = new EasyRandom().nextObject(ModelEquipTypeDTO.class);

        Mockito.doReturn(Optional.empty()).when(areaEquipamentRepository).findById(Mockito.any());
        Mockito.doReturn(modelEquipTypeDTO).when(modelEquipTypeService).findById(Mockito.any());
        Mockito.doReturn(new TechnicalStaffDTO()).when(technicalStaffService).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(areaEquipamentRepository).save(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> areaEquipamentService.createAreaEquipament(areaEquipamentDTO));
        assertEquals(MessageUtils.AREA_EQUIPAMENT_SAVE_ERROR.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void updateAreaEquipamentException() {
        AreaEquipamentDTO areaEquipamentDTO = new EasyRandom().nextObject(AreaEquipamentDTO.class);
        AreaEquipament areaEquipament = new EasyRandom().nextObject(AreaEquipament.class);
        ModelEquipTypeDTO modelEquipTypeDTO = new EasyRandom().nextObject(ModelEquipTypeDTO.class);
        Mockito.doReturn(Optional.of(areaEquipament)).when(areaEquipamentRepository).findById(Mockito.any());
        Mockito.doReturn(modelEquipTypeDTO).when(modelEquipTypeService).findById(Mockito.any());
        Mockito.doReturn(new TechnicalStaffDTO()).when(technicalStaffService).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(areaEquipamentRepository).save(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> areaEquipamentService.updateAreaEquipament(areaEquipamentDTO));
        assertEquals(MessageUtils.AREA_EQUIPAMENT_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test @SneakyThrows
    void updateAreaEquipament() {
        AreaEquipamentDTO areaEquipamentDTO = new EasyRandom().nextObject(AreaEquipamentDTO.class);
        AreaEquipament areaEquipament = new EasyRandom().nextObject(AreaEquipament.class);
        ModelEquipTypeDTO modelEquipTypeDTO = new EasyRandom().nextObject(ModelEquipTypeDTO.class);
        Mockito.doReturn(Optional.of(areaEquipament)).when(areaEquipamentRepository).findById(Mockito.any());
        Mockito.doReturn(modelEquipTypeDTO).when(modelEquipTypeService).findById(Mockito.any());
        Mockito.doReturn(new TechnicalStaffDTO()).when(technicalStaffService).findById(Mockito.any());
        MessageResponseDTO responseDTO = areaEquipamentService.updateAreaEquipament(areaEquipamentDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test
    void areaEquipamentReport() throws JRException, IOException {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).genericReportThreeColumns(Mockito.any(), Mockito.any());
        AreaEquipament areaEquipament =  new EasyRandom().nextObject(AreaEquipament.class);
        List<AreaEquipament> areaEquipaments = List.of(areaEquipament, areaEquipament);
        Pageable paging = PageRequest.of(0, 10);
        Page<AreaEquipament> pagedResult = new PageImpl<>(areaEquipaments, paging, areaEquipaments.size());
        Mockito.doReturn(pagedResult).when(areaEquipamentRepository).findAllByDiscontinuedFlagFalse((Pageable) Mockito.any());
        byte[] returnedReport = areaEquipamentService.areaEquipamentReport("", List.of(), List.of(), null);
        assertNotNull(returnedReport);
    }

    @Test
    void areaEquipamentReportEmpty() {
        List<AreaEquipament> areaEquipaments = List.of();
        Pageable paging = PageRequest.of(0, 10);
        Page<AreaEquipament> pagedResult = new PageImpl<>(areaEquipaments, paging, areaEquipaments.size());
        Mockito.doReturn(pagedResult).when(areaEquipamentRepository).findAllByDiscontinuedFlagFalse((Pageable) Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> areaEquipamentService.areaEquipamentReport("", null, null, null));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }
}