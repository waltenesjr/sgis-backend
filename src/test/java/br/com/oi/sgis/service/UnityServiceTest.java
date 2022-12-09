package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.*;
import br.com.oi.sgis.entity.view.WarrantyView;
import br.com.oi.sgis.exception.NotReprocessableUnityException;
import br.com.oi.sgis.exception.UnityException;
import br.com.oi.sgis.exception.UnityNotFoundException;
import br.com.oi.sgis.mapper.InstallationTransferMapper;
import br.com.oi.sgis.mapper.UnityMapper;
import br.com.oi.sgis.repository.*;
import br.com.oi.sgis.service.factory.ExtractUnityFactory;
import br.com.oi.sgis.service.factory.UnityFactory;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UnityServiceTest {

    @InjectMocks
    private UnityService unityService;
    @Mock
    private UnityRepository unityRepository;
    @Mock
    private UnityFactory unityFactory;
    @Mock
    private NasphService nasphService;
    @MockBean
    private UnityMapper unityMapper = UnityMapper.INSTANCE;
    @MockBean
    private InstallationTransferMapper installationTransferMapper = InstallationTransferMapper.INSTANCE;
    @Mock
    private ElectricalPropUnityRepository electricalPropUnityRepository;
    @Mock
    private ReprocessUnityService reprocessUnityService;
    @Mock
    private  WarrantyViewRepository warrantyViewRepository;
    @Mock
    private RepairTicketRepository repairTicketRepository;
    @Mock
    private ReportService reportService;
    @Mock
    private BarcodeFASRepository barcodeFASRepository;
    @Mock
    private ExtractUnityFactory extractUnityFactory;

    @Test
    void listAll(){
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        List<Unity> unities = List.of(unity);
        Mockito.doReturn(unities).when(unityRepository).findAll();

        List<UnityDTO> unitiesReturn = unityService.listAll();
        Assertions.assertEquals(unities.size(), unitiesReturn.size());
    }

    @Test
    void createRemovedFromSiteUnity(){
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        List<Unity> unities = List.of(unity);
        Pageable paging = PageRequest.of(0, 10, Sort.Direction.ASC, "id");
        Page<Unity> pagedResult = new PageImpl<>(unities, paging, unities.size());

        Mockito.doReturn(pagedResult).when(unityRepository).findAll(paging);
        PaginateResponseDTO<Object> unitiesReturn = unityService.listPaginated(0, 10, "id", "asc");
        Assertions.assertEquals(unities.size(), unitiesReturn.getData().size());
    }

    @Test
    void createNewSpareUnity() throws UnityException {
        UnityDTO unityDTO = UnityDTO.builder().id("12345").build();
        Unity unity = unityMapper.toModel(unityDTO);
        Mockito.doReturn(unity).when(unityRepository).save(Mockito.any());
        Mockito.doReturn(unity).when(unityFactory).makeNewSpareUnity(Mockito.any());
        MessageResponseDTO messageResponseDTO = unityService.createNewSpareUnity(unityDTO);

        Assertions.assertEquals(HttpStatus.CREATED, messageResponseDTO.getStatus());
    }

    @Test
    void shouldCreateNewSpareUnityPropElectricalException() throws UnityException {
        ElectricalPropUnityDTO electricalPropDTO = new EasyRandom().nextObject(ElectricalPropUnityDTO.class);
        UnityDTO unityDTO = UnityDTO.builder().id("12345").electricalProperties(List.of(electricalPropDTO)).build();
        Unity unity = unityMapper.toModel(unityDTO);
        Mockito.doReturn(unity).when(unityRepository).save(Mockito.any());
        Mockito.doReturn(unity).when(unityFactory).makeNewSpareUnity(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(electricalPropUnityRepository).saveAll(Mockito.any());

        Exception e = assertThrows(UnityException.class, () ->  unityService.createNewSpareUnity(unityDTO));
        Assertions.assertEquals(MessageUtils.UNITY_PROP_ELETRIC_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void shouldCreateRemovedFromSiteUnity() {
        UnityDTO unityDTO = UnityDTO.builder().id("12345").build();
        Unity unity = unityMapper.toModel(unityDTO);
        Mockito.doReturn(unity).when(unityRepository).save(Mockito.any());
        Mockito.doReturn(unity).when(unityFactory).makeRemovedFromSite(Mockito.any());
        MessageResponseDTO messageResponseDTO = unityService.createRemovedFromSiteUnity(unityDTO);

        Assertions.assertEquals(HttpStatus.CREATED, messageResponseDTO.getStatus());
    }

    @Test
    void shouldCreateRemovedFromSiteUnityWithException() {
        UnityDTO unityDTO = UnityDTO.builder().id("12345").build();
        Unity unity = unityMapper.toModel(unityDTO);
        Mockito.doThrow(IllegalArgumentException.class).when(unityRepository).save(Mockito.any());
        Mockito.doReturn(unity).when(unityFactory).makeRemovedFromSite(Mockito.any());
        assertThrows(IllegalArgumentException.class, () -> unityService.createRemovedFromSiteUnity(unityDTO));

    }

    @Test
    void shouldCreateNewSpareUnityWithException()  {
        UnityDTO unityDTO = UnityDTO.builder().id("12345").build();
        Mockito.doThrow(IllegalArgumentException.class).when(unityRepository).save(Mockito.any());

        assertThrows(IllegalArgumentException.class, () -> unityService.createNewSpareUnity(unityDTO));


    }

    @Test
    void findById(){
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.anyString());
        unity.setDeposit(Department.builder().build());
        unity.setTechnician(TechnicalStaff.builder().build());
        unity.setUnityCode(AreaEquipament.builder().build());

        UnityDTO unityDTOResponse = unityService.findById(unity.getId());

        Assertions.assertEquals(unity.getId(), unityDTOResponse.getId());
    }

    @Test
    void shouldDoThrowOnFindById(){
        Unity unity =   new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        Mockito.doReturn(Optional.empty()).when(unityRepository).findById(Mockito.anyString());
        assertThrows(UnityNotFoundException.class, () -> unityService.findById(unity.getId()));
    }

    @Test
    void searchByTermsPaginated(){
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        List<Unity> unities = List.of(unity);
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Unity> pagedResult = new PageImpl<>(unities, paging, unities.size());

        Mockito.doReturn(pagedResult).when(unityRepository).findAll(Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> unitiesReturn = unityService.searchByTermsPaginated(0, 10, List.of("id"), List.of("registerDate"), "");
        Assertions.assertEquals(unities.size(), unitiesReturn.getData().size());
    }

    @Test
    void shouldListAllWithSearchWithTerm(){
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        List<Unity> unities = List.of(unity);
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Unity> pagedResult = new PageImpl<>(unities, paging, unities.size());

        Mockito.doReturn(pagedResult).when(unityRepository).findLike(Mockito.anyString(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> unitiesReturn = unityService.searchByTermsPaginated(0, 10, List.of("id"), List.of("registerDate"), "RJ-");
        Assertions.assertEquals(unities.size(), unitiesReturn.getData().size());
    }

    @Test
    void listUnitiesForPropertyTransf(){
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        List<Unity> unities = List.of(unity);
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Unity> pagedResult = new PageImpl<>(unities, paging, unities.size());

        Mockito.doReturn(pagedResult).when(unityRepository).findAllBySituationCodeContains(Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> unitiesReturn = unityService.listUnitiesForPropertyTransf(0, 10, List.of("id"), List.of("registerDate"), "");
        Assertions.assertEquals(unities.size(), unitiesReturn.getData().size());
    }

    @Test
    void shouldListlistUnitiesForPropertyTransfWithTerm(){
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        List<Unity> unities = List.of(unity);
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Unity> pagedResult = new PageImpl<>(unities, paging, unities.size());

        Mockito.doReturn(pagedResult).when(unityRepository).findAllBySituationCodeContainsLike(Mockito.anyString(), Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> unitiesReturn = unityService.listUnitiesForPropertyTransf(0, 10, List.of("id"), List.of("registerDate"), "RJ-");
        Assertions.assertEquals(unities.size(), unitiesReturn.getData().size());
    }

    @Test
    void updatePropertyTransf(){
        FiscalDocumentDTO fiscalDocumentDTO = new FiscalDocumentDTO();
        fiscalDocumentDTO.setCurrencyType(CurrencyTypeDTO.builder().id("RE").build());
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        unityDTO.setFiscalDocument(fiscalDocumentDTO);
        Unity unity = unityMapper.toModel(unityDTO);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        MessageResponseDTO expectedResponse = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();

        Mockito.doReturn(expectedResponse).when(nasphService).transfProperty(Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.anyString());

        MessageResponseDTO messageResponseDTO = unityService.updatePropertyTransf(unityDTO);

        Assertions.assertEquals(expectedResponse.getStatus(), messageResponseDTO.getStatus());
    }

    @Test
    void listUnitiesForPropertyRepos(){
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        List<Unity> unities = List.of(unity);
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Unity> pagedResult = new PageImpl<>(unities, paging, unities.size());

        Mockito.doReturn(pagedResult).when(unityRepository).findAllForPropertyRepos(Mockito.any(), Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> unitiesReturn = unityService.listUnitiesForPropertyRepos(0, 10, List.of("id"), List.of("registerDate"), "");
        Assertions.assertEquals(unities.size(), unitiesReturn.getData().size());
    }

    @Test
    void listUnitiesForUpdateSituation(){
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        List<Unity> unities = List.of(unity);
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Unity> pagedResult = new PageImpl<>(unities, paging, unities.size());

        Mockito.doReturn(pagedResult).when(unityRepository).findAllForPropertyReposContains(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> unitiesReturn = unityService.listUnitiesForPropertyRepos(0, 10, List.of("id"), List.of("registerDate"), "RJ-");
        Assertions.assertEquals(unities.size(), unitiesReturn.getData().size());
    }

    @Test
    void updatePropertyRepo(){
        FiscalDocumentDTO fiscalDocumentDTO = new FiscalDocumentDTO();
        fiscalDocumentDTO.setCurrencyType(CurrencyTypeDTO.builder().id("RE").build());
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        unityDTO.setFiscalDocument(fiscalDocumentDTO);
        Unity unity = unityMapper.toModel(unityDTO);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        MessageResponseDTO expectedResponse = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();

        Mockito.doReturn(expectedResponse).when(nasphService).repoProperty(Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.anyString());

        MessageResponseDTO messageResponseDTO = unityService.updatePropertyRepo(unityDTO);

        Assertions.assertEquals(expectedResponse.getStatus(), messageResponseDTO.getStatus());
    }

    @Test
    void shouldListUnitiesForUpdateSituationWithoutTerm(){
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        List<Unity> unities = List.of(unity);
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Unity> pagedResult = new PageImpl<>(unities, paging, unities.size());

        Mockito.doReturn(pagedResult).when(unityRepository).findAllBySituationCodeContainsAndDepartment(Mockito.any(), Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> unitiesReturn = unityService.listUnitiesForUpdateSituation(0, 10, List.of("id"), List.of("registerDate"), "");
        Assertions.assertEquals(unities.size(), unitiesReturn.getData().size());
    }

    @Test
    void shouldListUnitiesForUpdateSituationWithTerm(){
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        List<Unity> unities = List.of(unity);
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Unity> pagedResult = new PageImpl<>(unities, paging, unities.size());

        Mockito.doReturn(pagedResult).when(unityRepository).findAllBySituationCodeContainsAndDepartment(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> unitiesReturn = unityService.listUnitiesForUpdateSituation(0, 10, List.of("id"), List.of("registerDate"), "RJ-");
        Assertions.assertEquals(unities.size(), unitiesReturn.getData().size());
    }

    @Test
    void shouldUpdateSituation(){
        UnitySituationDTO unitySituationDTO = new EasyRandom().nextObject(UnitySituationDTO.class);
        MessageResponseDTO expectedResponse = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();

        Mockito.doReturn(expectedResponse).when(nasphService).updateUnitySituation(Mockito.any());
        MessageResponseDTO messageResponseDTO = unityService.updateUnitySituation(unitySituationDTO);
        Assertions.assertEquals(expectedResponse.getStatus(), messageResponseDTO.getStatus());
    }

    @Test
    void shouldListUnitiesForUnitySwap(){
        Unity unity = new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        List<Unity> unities = List.of(unity);
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Unity> pagedResult = new PageImpl<>(unities, paging, unities.size());

        Mockito.doReturn(pagedResult).when(unityRepository).findAllBySituationCodeContainsAndDepartment(Mockito.any(), Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> unitiesReturn = unityService.listUnitiesForUnitySwap(0, 10, List.of("id"), List.of("registerDate"), "");
        Assertions.assertEquals(unities.size(), unitiesReturn.getData().size());
    }

    @Test
    void shouldListUnitiesForUnitySwapWithTerm(){
        Unity unity = new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        List<Unity> unities = List.of(unity);
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Unity> pagedResult = new PageImpl<>(unities, paging, unities.size());

        Mockito.doReturn(pagedResult).when(unityRepository).findAllBySituationCodeContainsAndDepartment(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> unitiesReturn = unityService.listUnitiesForUnitySwap(0, 10, List.of("id"), List.of("registerDate"), "RJ-");
        Assertions.assertEquals(unities.size(), unitiesReturn.getData().size());
    }

    @Test
    void shouldSwapUnity(){
        UnitySwapDTO unitySwapDTO = new EasyRandom().nextObject(UnitySwapDTO.class);
        MessageResponseDTO expectedResponse = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();

        Mockito.doReturn(expectedResponse).when(nasphService).updateUnitySwap(Mockito.any());
        MessageResponseDTO messageResponseDTO = unityService.updateUnitySwap(unitySwapDTO);
        Assertions.assertEquals(expectedResponse.getStatus(), messageResponseDTO.getStatus());
    }

    @Test
    void shouldListUnitiesToPlanInstallation(){
        Unity unity = new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        unity.setSituationCode(Situation.builder().id("DIS").build());
        List<Unity> unities = List.of(unity);
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Unity> pagedResult = new PageImpl<>(unities, paging, unities.size());

        Mockito.doReturn(pagedResult).when(unityRepository).findAllBySituationCodeContainsAndDepartment(Mockito.any(), Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> unitiesReturn = unityService.listUnitiesToPlanInstallation(0, 10, List.of("id"), List.of("registerDate"), "");
        Assertions.assertEquals(unities.size(), unitiesReturn.getData().size());
    }

    @Test
    void listUnitiesToPlanInstallation(){
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        List<Unity> unities = List.of(unity);
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Unity> pagedResult = new PageImpl<>(unities, paging, unities.size());

        Mockito.doReturn(pagedResult).when(unityRepository).findAllBySituationCodeContainsAndDepartment(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> unitiesReturn = unityService.listUnitiesToPlanInstallation(0, 10, List.of("id"), List.of("registerDate"), "RJ-");
        Assertions.assertEquals(unities.size(), unitiesReturn.getData().size());
    }

    @Test
    void updatePlanInstallation() throws UnityNotFoundException {
        PlanInstallationDTO planInstallationDTO = new EasyRandom().nextObject(PlanInstallationDTO.class);
        MessageResponseDTO expectedResponse = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Unity unity = new EasyRandom().nextObject(Unity.class);
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Mockito.doReturn(expectedResponse).when(nasphService).updatePlanInstallation(Mockito.any());
        MessageResponseDTO messageResponseDTO = unityService.updatePlanInstallation(planInstallationDTO);
        Assertions.assertEquals(expectedResponse.getStatus(), messageResponseDTO.getStatus());
    }

    @Test
    void updateUnityWriteOff(){
        UnityWriteOffDTO unityWriteOffDTO = new EasyRandom().nextObject(UnityWriteOffDTO.class);
        MessageResponseDTO expectedResponse = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();

        Mockito.doReturn(expectedResponse).when(nasphService).updateUnityWriteOff(Mockito.any());
        MessageResponseDTO messageResponseDTO = unityService.updateUnityWriteOff(unityWriteOffDTO);
        Assertions.assertEquals(expectedResponse.getStatus(), messageResponseDTO.getStatus());
    }

    @Test
    void registerBoNumber() throws UnityNotFoundException {
        RegisterBoDTO registerBoDTO = new EasyRandom().nextObject(RegisterBoDTO.class);
        MessageResponseDTO expectedResponse = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Unity unity = Unity.builder().id("12345").instalationReason("IFM").build();
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.anyString());

        MessageResponseDTO messageResponseDTO = unityService.registerBoNumber(registerBoDTO);
        Assertions.assertEquals(expectedResponse.getStatus(), messageResponseDTO.getStatus());
        Assertions.assertEquals(registerBoDTO.getBoNumber(), unity.getBoNumber());
    }

    @Test
    void registerBoNumberBOAlreadyRegistered() {
        RegisterBoDTO registerBoDTO = new EasyRandom().nextObject(RegisterBoDTO.class);
        Unity unity = Unity.builder().id("12345").instalationReason("IFM").boNumber("1574845").build();
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.anyString());

        Exception e = assertThrows(IllegalArgumentException.class,()-> unityService.registerBoNumber(registerBoDTO));
        Assertions.assertEquals(String.format(MessageUtils.UNITY_BO_ALREADY_EXISTS_ERROR.getDescription(), unity.getBoNumber()), e.getMessage());
    }

    @Test
    void registerBoNumberInstalationReason() {
        RegisterBoDTO registerBoDTO = new EasyRandom().nextObject(RegisterBoDTO.class);
        Unity unity = Unity.builder().id("12345").instalationReason("IRP").build();
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.anyString());

        Exception e = assertThrows(IllegalArgumentException.class,()-> unityService.registerBoNumber(registerBoDTO));
        Assertions.assertEquals(MessageUtils.UNITY_BO_REASON_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void shouldListUnitiesToRecoverItem(){
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        List<Unity> unities = List.of(unity);
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Unity> pagedResult = new PageImpl<>(unities, paging, unities.size());

        Mockito.doReturn(pagedResult).when(unityRepository).findAllBySituationCodeContainsAndDepartment(Mockito.any(), Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> unitiesReturn = unityService.listUnitiesForItemRecover(0, 10, List.of("id"), List.of("registerDate"), "");
        Assertions.assertEquals(unities.size(), unitiesReturn.getData().size());
    }

    @Test
    void shouldListUnitiesToRecoverItemWithTerm(){
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        List<Unity> unities = List.of(unity);
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        Page<Unity> pagedResult = new PageImpl<>(unities, paging, unities.size());

        Mockito.doReturn(pagedResult).when(unityRepository).findAllBySituationCodeContainsAndDepartment(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any(Pageable.class));
        PaginateResponseDTO<Object> unitiesReturn = unityService.listUnitiesForItemRecover(0, 10, List.of("id"), List.of("registerDate"), "RJ-");
        Assertions.assertEquals(unities.size(), unitiesReturn.getData().size());
    }

    @Test
    void shouldUpdateRecoverItem(){
        RecoverItemDTO recoverItemDTO = new EasyRandom().nextObject(RecoverItemDTO.class);
        MessageResponseDTO expectedResponse = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());

        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());

        Mockito.doReturn(expectedResponse).when(nasphService).recoverItem(Mockito.any());
        MessageResponseDTO messageResponseDTO = unityService.recoverItem(recoverItemDTO);
        Assertions.assertEquals(expectedResponse.getStatus(), messageResponseDTO.getStatus());
    }

    @Test
    void shouldUpdateRecoverItemGetLocationAndStationByUnity(){
        RecoverItemDTO recoverItemDTO = RecoverItemDTO.builder().unityId("12345").build();
        MessageResponseDTO expectedResponse = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();
        Unity unity = Unity.builder().id("12345").station(Station.builder().id("station").build()).location("12lK").build();

        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());

        Mockito.doReturn(expectedResponse).when(nasphService).recoverItem(Mockito.any());
        MessageResponseDTO messageResponseDTO = unityService.recoverItem(recoverItemDTO);
        Assertions.assertEquals(expectedResponse.getStatus(), messageResponseDTO.getStatus());
    }

    @Test
    void sapReprocessUnitySituation() throws NotReprocessableUnityException {
        ReprocessableUnityDTO reprocessableUnityDTO = new EasyRandom().nextObject(ReprocessableUnityDTO.class);
        UnityToReprocessSapDTO unityToReprocessSapDTO = new EasyRandom().nextObject(UnityToReprocessSapDTO.class);
        Mockito.doReturn(reprocessableUnityDTO).when(unityRepository).findReprocessableUnity(Mockito.any());
        Mockito.doReturn(unityToReprocessSapDTO).when(reprocessUnityService).reprocessUnity(Mockito.any());

        UnityToReprocessSapDTO reprocessUnityReturn = unityService.sapUnityReprocessable("unityId");

        Assertions.assertEquals(unityToReprocessSapDTO.getUnityId(), reprocessUnityReturn.getUnityId());

    }

    @Test
    void reprocessSapUnity() {
        ReprocessableUnityDTO reprocessableUnityDTO = new EasyRandom().nextObject(ReprocessableUnityDTO.class);
        UnityToReprocessSapDTO unityToReprocessSapDTO = new EasyRandom().nextObject(UnityToReprocessSapDTO.class);
        Mockito.doReturn(reprocessableUnityDTO).when(unityRepository).findReprocessableUnity(Mockito.any());

        MessageResponseDTO expectedResponse = MessageResponseDTO.builder().status(HttpStatus.CREATED).build();

        Mockito.doReturn(expectedResponse).when(nasphService).reprocessSapUnity(Mockito.any());
        MessageResponseDTO messageResponseDTO = unityService.reprocessSapUnity(unityToReprocessSapDTO);
        Assertions.assertEquals(expectedResponse.getStatus(), messageResponseDTO.getStatus());
    }

    @Test
    void updateUnity() {
        UnityDTO unityToUpdate = new EasyRandom().nextObject(UnityDTO.class);
        Unity unityRegister = new EasyRandom().nextObject(Unity.class);
        unityRegister.setSituationCode(Situation.builder().id("DIS").build());
        unityRegister.setSapStatus("0");
        Mockito.doReturn(Optional.of(unityRegister)).when(unityRepository).findById(Mockito.any());

        MessageResponseDTO responseDTO = unityService.updateUnity(unityToUpdate);
        Assertions.assertEquals(HttpStatus.OK, responseDTO.getStatus());
    }

    @Test
    void updateUnitySapStatusError() {
        UnityDTO unityToUpdate = new EasyRandom().nextObject(UnityDTO.class);
        Unity unityRegister = new EasyRandom().nextObject(Unity.class);
        unityRegister.setSituationCode(Situation.builder().id("DIS").build());
        unityRegister.setSapStatus("1");
        Mockito.doReturn(Optional.of(unityRegister)).when(unityRepository).findById(Mockito.any());

        Exception e = assertThrows(UnityException.class, () -> unityService.updateUnity(unityToUpdate));
        Assertions.assertEquals(MessageUtils.UNITY_UPDATE_SAP_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void updateUnityError() {
        UnityDTO unityToUpdate = new EasyRandom().nextObject(UnityDTO.class);
        Unity unityRegister = new EasyRandom().nextObject(Unity.class);
        unityRegister.setSituationCode(Situation.builder().id("DIS").build());
        unityRegister.setSapStatus("0");
        Mockito.doReturn(Optional.of(unityRegister)).when(unityRepository).findById(Mockito.any());
        Mockito.doThrow(SqlScriptParserException.class).when(unityRepository).updateUnity(Mockito.any());
        Exception e = assertThrows(UnityException.class, () -> unityService.updateUnity(unityToUpdate));
        Assertions.assertEquals(MessageUtils.UNITY_UPDATE_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void getInstallationTransfer(){
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.anyString());
        unity.setDeposit(Department.builder().build());
        unity.setTechnician(TechnicalStaff.builder().build());
        unity.setUnityCode(AreaEquipament.builder().build());

        InstallationTransferDTO installationTransferDTOResponse = unityService.getInstallationTransfer(unity.getId());

        Assertions.assertEquals(unity.getBaNumber(), installationTransferDTOResponse.getBaNumber());
    }

    @Test
    void getInstallationTransferException(){
        Unity unity =   new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        Mockito.doReturn(Optional.empty()).when(unityRepository).findById(Mockito.anyString());
        assertThrows(UnityNotFoundException.class, () -> unityService.getInstallationTransfer(unity.getId()));
    }

    @Test
    void getWarranty() throws UnityNotFoundException {
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        WarrantyView warrantyView = new EasyRandom().nextObject(WarrantyView.class);
        Mockito.doReturn(List.of(warrantyView)).when(warrantyViewRepository).findByUnityId(Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.anyString());

        List<WarrantyViewDTO> warrantyViews = unityService.getWarranty(unity.getId());

        Assertions.assertEquals(warrantyView.getUnityId(), warrantyViews.get(0).getUnityId());

    }

    @Test
    void getWarrantyException(){
        Unity unity =   new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        Mockito.doReturn(Optional.empty()).when(unityRepository).findById(Mockito.anyString());
        assertThrows(UnityNotFoundException.class, () -> unityService.getWarranty(unity.getId()));
    }

    @Test
    void getTickets() throws UnityNotFoundException {
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());
        RepairTicket repairTicket = new EasyRandom().nextObject(RepairTicket.class);
        Mockito.doReturn(List.of(repairTicket)).when(repairTicketRepository).findByUnityId(Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.anyString());

        List<RepairTicketDTO> tickets = unityService.getTickets(unity.getId());

        Assertions.assertEquals(repairTicket.getId(), tickets.get(0).getBrNumber());

    }

    @Test
    void generalAcceptance() throws UnityNotFoundException, UnityException {
        UnityAcceptanceDTO unityAcceptanceDTO = new EasyRandom().nextObject(UnityAcceptanceDTO.class);
        Unity unity = Unity.builder().id("12345").sapStatus("0").build();

        MessageResponseDTO expectedResponseDTO =  MessageResponseDTO.builder().message("Teste").status(HttpStatus.OK).build();
        Mockito.doReturn(expectedResponseDTO).when(nasphService).generalAcceptance(unityAcceptanceDTO);
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.anyString());

        MessageResponseDTO responseDTO = unityService.generalAcceptance(unityAcceptanceDTO);
        Assertions.assertEquals(expectedResponseDTO.getStatus(), responseDTO.getStatus());
    }

    @Test
    void generalAcceptanceSapError() {
        UnityAcceptanceDTO unityAcceptanceDTO = new EasyRandom().nextObject(UnityAcceptanceDTO.class);
        Unity unity =  new EasyRandom().nextObject(Unity.class);
        unity.setSituationCode(Situation.builder().id("DIS").build());

        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.anyString());

        Exception e = assertThrows(UnityException.class,()->unityService.generalAcceptance(unityAcceptanceDTO));
        Assertions.assertEquals(MessageUtils.UNITY_UPDATE_SAP_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void changeBarcode() {
        ChangeBarcodeDTO changeBarcodeDTO = ChangeBarcodeDTO.builder().newBarcode("new").oldbarcode("old").build();
        Unity unity = Unity.builder().id("12345").sapStatus("0").build();
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById("old");
        Mockito.doReturn(Optional.empty()).when(unityRepository).findById("new");
        MessageResponseDTO expectedResponseDTO =  MessageResponseDTO.builder().message("Teste").status(HttpStatus.OK).build();
        Mockito.doReturn(expectedResponseDTO).when(nasphService).changeBarcode(Mockito.any());
        MessageResponseDTO responseDTO = unityService.changeBarcode(changeBarcodeDTO);
        Assertions.assertEquals(expectedResponseDTO.getStatus(), responseDTO.getStatus());
    }

    @Test
    void changeBarcodeException() {
        ChangeBarcodeDTO changeBarcodeDTO = ChangeBarcodeDTO.builder().newBarcode("new").oldbarcode("old").build();
        Unity unity = Unity.builder().id("12345").sapStatus("0").build();
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById("old");
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById("new");
        Exception e = assertThrows(IllegalArgumentException.class, () -> unityService.changeBarcode(changeBarcodeDTO));
        assertEquals(MessageUtils.BARCODE_ALREADY_EXISTS.getDescription(), e.getMessage());
    }

    @Test
    void trackingRecord() throws JRException, IOException {
        UnityDTO unity = new EasyRandom().nextObject(UnityDTO.class);
        TrackingRecordDTO trackingRecord =   new TrackingRecordDTO("123", "123", "123" ,"123",
                "123", "123", "123" ,"123","123");
        byte[] report = new byte[50];
        Mockito.doReturn(List.of(trackingRecord)).when(unityRepository).getTrackingRecord(Mockito.any());
        Mockito.doReturn("001").when(unityRepository).nextFasSequence();
        Mockito.doReturn(report).when(reportService).trackingRecordReport(Mockito.any(), Mockito.any());

        byte[] trackingRecordReport = unityService.trackingRecord(List.of(unity));
        assertNotNull(trackingRecordReport);
        assertEquals(report, trackingRecordReport);
        Mockito.verify(barcodeFASRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void trackingRecordException() throws JRException, IOException {
        UnityDTO unity = new EasyRandom().nextObject(UnityDTO.class);
        TrackingRecordDTO trackingRecord =   new TrackingRecordDTO("123", "123", "123" ,"123",
                "123", "123", "123" ,"123","123");
        byte[] report = new byte[50];
        Mockito.doReturn(List.of(trackingRecord)).when(unityRepository).getTrackingRecord(Mockito.any());
        Mockito.doReturn("001").when(unityRepository).nextFasSequence();
        Mockito.doThrow(JRException.class).when(reportService).trackingRecordReport(Mockito.any(), Mockito.any());
        List<UnityDTO> unities = List.of(unity);
        Exception e = assertThrows(IllegalArgumentException.class, ()->unityService.trackingRecord(unities));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());

    }

    @Test
    void unitExtractionReport() throws JRException, IOException {
        Unity unity = new EasyRandom().nextObject(Unity.class);
        byte[] report = new byte[50];
        UnitExtractionDTO filterDTO = new EasyRandom().nextObject(UnitExtractionDTO.class);
        UnitExtractionReportDTO dto = new EasyRandom().nextObject(UnitExtractionReportDTO.class);
        Mockito.doReturn(List.of(unity)).when(unityRepository).findForExtraction(Mockito.any());
        Mockito.doReturn(dto).when(extractUnityFactory).createExtractUnity(Mockito.any());
        Mockito.doReturn(report).when(reportService).unitExtractionReport(Mockito.any(), Mockito.any());

        byte[] unityExtraction = unityService.unitExtractionReport(filterDTO);
        assertNotNull(unityExtraction);
        assertEquals(report, unityExtraction);

    }

    @Test
    void unitExtractionReportError() throws JRException, IOException {
        Unity unity = new EasyRandom().nextObject(Unity.class);
        UnitExtractionDTO filterDTO = new EasyRandom().nextObject(UnitExtractionDTO.class);
        UnitExtractionReportDTO dto = new EasyRandom().nextObject(UnitExtractionReportDTO.class);
        Mockito.doReturn(List.of(unity)).when(unityRepository).findForExtraction(Mockito.any());
        Mockito.doReturn(dto).when(extractUnityFactory).createExtractUnity(Mockito.any());
        Mockito.doThrow(JRException.class).when(reportService).unitExtractionReport(Mockito.any(), Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->unityService.unitExtractionReport(filterDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());

    }

    @Test
    void unitExtractionReportEmpty() {
        UnitExtractionDTO filterDTO = new EasyRandom().nextObject(UnitExtractionDTO.class);
        Mockito.doReturn(List.of()).when(unityRepository).findForExtraction(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->unityService.unitExtractionReport(filterDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }
}
