package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.enums.InstallationReasonEnum;
import br.com.oi.sgis.enums.ReasonForWriteOffEnum;
import br.com.oi.sgis.exception.NotReprocessableUnityException;
import br.com.oi.sgis.exception.UnityException;
import br.com.oi.sgis.exception.UnityNotFoundException;
import br.com.oi.sgis.service.CompositionService;
import br.com.oi.sgis.service.UnityService;
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
class UnityControllerTest {

    @InjectMocks
    private UnityController unityController;

    @Mock
    private UnityService unityService;

    @Mock
    private CompositionService compositionService;

    @Test
    void shouldCreateNewSpareUnity(){
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        MessageResponseDTO expectedResponseDTO =  MessageResponseDTO.builder().message("Teste").status(HttpStatus.CREATED).build();
        Mockito.doReturn(expectedResponseDTO).when(unityService).createNewSpareUnity(Mockito.any());

        MessageResponseDTO response = unityController.createNewSpareUnity(unityDTO);
        Assertions.assertEquals(expectedResponseDTO.getStatus(), response.getStatus());
    }

    @Test
    void shouldCreateRemovedFromSiteUnity(){
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        MessageResponseDTO expectedResponseDTO =  MessageResponseDTO.builder().message("Teste").status(HttpStatus.CREATED).build();
        Mockito.doReturn(expectedResponseDTO).when(unityService).createRemovedFromSiteUnity(Mockito.any());

        MessageResponseDTO response = unityController.createUnityRemovedFromSite(unityDTO);
        Assertions.assertEquals(expectedResponseDTO.getStatus(), response.getStatus());
    }

    @Test
    void shouldFindUnityById() {
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        Mockito.doReturn(unityDTO).when(unityService).findById(Mockito.any());

        UnityDTO unityDtoResponse = unityController.findById("12345");

        Assertions.assertEquals(unityDTO.getId(), unityDtoResponse.getId());
    }

    @Test
    void shouldListAllWithSearchPaginated(){
        List<Unity> unities = new EasyRandom().objects(Unity.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(unities, paging, unities.size()));

        Mockito.doReturn(expectedResponse).when(unityService).searchByTermsPaginated(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<Object>> response = unityController.listAllWithSearch(0, 10,  List.of("id"), List.of("registerDate"), "");

        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void shouldListAllPropertyTransfPaginated(){
        List<Unity> unities = new EasyRandom().objects(Unity.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(unities, paging, unities.size()));

        Mockito.doReturn(expectedResponse).when(unityService).listUnitiesForPropertyTransf(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<Object>> response = unityController.listAllPropertyTransf(0, 10,  List.of("id"), List.of("registerDate"), "");

        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void shouldPropertyTransf(){
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        MessageResponseDTO expectedResponseDTO =  MessageResponseDTO.builder().message("Teste").status(HttpStatus.CREATED).build();
        Mockito.doReturn(expectedResponseDTO).when(unityService).updatePropertyTransf(Mockito.any());

        MessageResponseDTO response = unityController.updatePropertyTransf(unityDTO);
        Assertions.assertEquals(expectedResponseDTO.getStatus(), response.getStatus());
    }

    @Test
    void shouldListAllPropertyRepoPaginated(){
        List<Unity> unities = new EasyRandom().objects(Unity.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(unities, paging, unities.size()));

        Mockito.doReturn(expectedResponse).when(unityService).listUnitiesForPropertyRepos(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<Object>> response = unityController.listAllPropertyRepos(0, 10,  List.of("id"), List.of("registerDate"), "");

        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }

    @Test
    void shouldPropertyRepo(){
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        MessageResponseDTO expectedResponseDTO =  MessageResponseDTO.builder().message("Teste").status(HttpStatus.CREATED).build();
        Mockito.doReturn(expectedResponseDTO).when(unityService).updatePropertyRepo(Mockito.any());

        MessageResponseDTO response = unityController.updatePropertyRepos(unityDTO);
        Assertions.assertEquals(expectedResponseDTO.getStatus(), response.getStatus());
    }
    @Test
    void shouldListAllUpdateItemSituationPaginated(){
        List<Unity> unities = new EasyRandom().objects(Unity.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(unities, paging, unities.size()));

        Mockito.doReturn(expectedResponse).when(unityService).listUnitiesForUpdateSituation(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<Object>> response = unityController.listAllUpdateItemSituation(0, 10,  List.of("id"), List.of("registerDate"), "");

        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }
    @Test
    void shouldupdateUnitySituation(){
        UnitySituationDTO unitySituationDTO = new EasyRandom().nextObject(UnitySituationDTO.class);
        MessageResponseDTO expectedResponseDTO =  MessageResponseDTO.builder().message("Teste").status(HttpStatus.CREATED).build();
        Mockito.doReturn(expectedResponseDTO).when(unityService).updateUnitySituation(Mockito.any());

        MessageResponseDTO response = unityController.updateUnitySituation(unitySituationDTO);
        Assertions.assertEquals(expectedResponseDTO.getStatus(), response.getStatus());
    }
    @Test
    void shouldListAllUpdateItemSwapPaginated(){
        List<Unity> unities = new EasyRandom().objects(Unity.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(unities, paging, unities.size()));

        Mockito.doReturn(expectedResponse).when(unityService).listUnitiesForUnitySwap(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<Object>> response = unityController.listAllUpdateItemSwap(0, 10,  List.of("id"), List.of("registerDate"), "");

        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }
    @Test
    void shouldupdateUnitySwap(){
        UnitySwapDTO unitySwapDTO = new EasyRandom().nextObject(UnitySwapDTO.class);
        MessageResponseDTO expectedResponseDTO =  MessageResponseDTO.builder().message("Teste").status(HttpStatus.CREATED).build();
        Mockito.doReturn(expectedResponseDTO).when(unityService).updateUnitySwap(Mockito.any());

        MessageResponseDTO response = unityController.updateUnitySwap(unitySwapDTO);
        Assertions.assertEquals(expectedResponseDTO.getStatus(), response.getStatus());
    }

    @Test
    void listAllToPlanInstallation(){
        List<Unity> unities = new EasyRandom().objects(Unity.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(unities, paging, unities.size()));

        Mockito.doReturn(expectedResponse).when(unityService).listUnitiesToPlanInstallation(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<Object>> response = unityController.listAllToPlanInstallation(0, 10,  List.of("id"), List.of("registerDate"), "");

        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }
    @Test
    void planInstallation() throws UnityNotFoundException {
        PlanInstallationDTO planInstallationDTO = new EasyRandom().nextObject(PlanInstallationDTO.class);
        MessageResponseDTO expectedResponseDTO =  MessageResponseDTO.builder().message("Teste").status(HttpStatus.CREATED).build();
        Mockito.doReturn(expectedResponseDTO).when(unityService).updatePlanInstallation(Mockito.any());

        MessageResponseDTO response = unityController.planInstallation(planInstallationDTO);
        Assertions.assertEquals(expectedResponseDTO.getStatus(), response.getStatus());
    }

    @Test
    void installationReasons(){
        List<InstallationReasonDTO> reasons = unityController.installationReasons();
        Assertions.assertEquals(InstallationReasonEnum.values().length, reasons.size());
    }

    @Test
    void writeOffUnity(){
        UnityWriteOffDTO unityWriteOffDTO = new EasyRandom().nextObject(UnityWriteOffDTO.class);
        MessageResponseDTO expectedResponseDTO =  MessageResponseDTO.builder().message("Teste").status(HttpStatus.CREATED).build();
        Mockito.doReturn(expectedResponseDTO).when(unityService).updateUnityWriteOff(Mockito.any());

        MessageResponseDTO response = unityController.writeOffUnity(unityWriteOffDTO);
        Assertions.assertEquals(expectedResponseDTO.getStatus(), response.getStatus());
    }

    @Test
    void writeOffReasons(){
        List<ReasonForWriteOffDTO> reasons = unityController.writeOffReasons();
        Assertions.assertEquals(ReasonForWriteOffEnum.values().length, reasons.size());
    }

    @Test
    void registerBoNumber() throws UnityNotFoundException {
        RegisterBoDTO registerBoDTO = new EasyRandom().nextObject(RegisterBoDTO.class);
        MessageResponseDTO expectedResponseDTO =  MessageResponseDTO.builder().message("Teste").status(HttpStatus.CREATED).build();
        Mockito.doReturn(expectedResponseDTO).when(unityService).registerBoNumber(Mockito.any());

        MessageResponseDTO response = unityController.registerBoNumber(registerBoDTO);
        Assertions.assertEquals(expectedResponseDTO.getStatus(), response.getStatus());
    }
    @Test
    void listAllToRecoverItemDTO(){
        List<Unity> unities = new EasyRandom().objects(Unity.class, 5).collect(Collectors.toList());
        List<Sort.Order> orders = List.of(Sort.Order.asc("id"), Sort.Order.desc("registerDate"));
        Pageable paging = PageRequest.of(0, 10, Sort.by(orders));
        PaginateResponseDTO<Object> expectedResponse = PageableUtil.paginate(new PageImpl(unities, paging, unities.size()));

        Mockito.doReturn(expectedResponse).when(unityService).listUnitiesForItemRecover(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        ResponseEntity<PaginateResponseDTO<Object>> response = unityController.listAllToRecoverItem(0, 10,  List.of("id"), List.of("registerDate"), "");

        Assertions.assertEquals(expectedResponse.getPaginate().getTotalItens(), response.getBody().getPaginate().getTotalItens());
        Assertions.assertEquals(expectedResponse.getData().get(0), response.getBody().getData().get(0));
    }
    @Test
    void recoverItem(){
        RecoverItemDTO recoverItemDTO = new EasyRandom().nextObject(RecoverItemDTO.class);
        MessageResponseDTO expectedResponseDTO =  MessageResponseDTO.builder().message("Teste").status(HttpStatus.CREATED).build();
        Mockito.doReturn(expectedResponseDTO).when(unityService).recoverItem(Mockito.any());

        MessageResponseDTO response = unityController.recoverItem(recoverItemDTO);
        Assertions.assertEquals(expectedResponseDTO.getStatus(), response.getStatus());
    }

    @Test
    void sapReprocess() throws NotReprocessableUnityException {
        UnityToReprocessSapDTO unityToReprocessSapDTO = new EasyRandom().nextObject(UnityToReprocessSapDTO.class);
        Mockito.doReturn(unityToReprocessSapDTO).when(unityService).sapUnityReprocessable(Mockito.any());

        UnityToReprocessSapDTO unityReprocessableReturn = unityController.sapReprocess("unityId");
        Assertions.assertEquals(unityToReprocessSapDTO.getUnityId(), unityReprocessableReturn.getUnityId());
    }

    @Test
    void testSapReprocess() {
        UnityToReprocessSapDTO unityToReprocessSapDTO = new EasyRandom().nextObject(UnityToReprocessSapDTO.class);
        MessageResponseDTO expectedResponseDTO =  MessageResponseDTO.builder().message("Teste").status(HttpStatus.CREATED).build();
        Mockito.doReturn(expectedResponseDTO).when(unityService).reprocessSapUnity(Mockito.any());

        MessageResponseDTO response = unityController.sapReprocess(unityToReprocessSapDTO);
        Assertions.assertEquals(expectedResponseDTO.getStatus(), response.getStatus());

    }

    @Test
    void updateUnity(){
        UnityDTO unityToUpdate = new EasyRandom().nextObject(UnityDTO.class);
        MessageResponseDTO expectedResponseDTO =  MessageResponseDTO.builder().message("Teste").status(HttpStatus.OK).build();
        Mockito.doReturn(expectedResponseDTO).when(unityService).updateUnity(Mockito.any());

        MessageResponseDTO response = unityController.updateUnity(unityToUpdate);
        Assertions.assertEquals(expectedResponseDTO.getStatus(), response.getStatus());
    }

    @Test
    void getInstallationTransfer() {
        InstallationTransferDTO installationTransferDTO = new EasyRandom().nextObject(InstallationTransferDTO.class);
        Mockito.doReturn(installationTransferDTO).when(unityService).getInstallationTransfer(Mockito.any());

        InstallationTransferDTO installationTransferDTOResponse = unityController.getInstallationTransfer("12345");

        Assertions.assertEquals(installationTransferDTO.getBaNumber(), installationTransferDTOResponse.getBaNumber());
    }

    @Test
    void getWarranty() throws UnityNotFoundException {
        WarrantyViewDTO warrantyViewDTO = new EasyRandom().nextObject(WarrantyViewDTO.class);
        Mockito.doReturn(List.of(warrantyViewDTO)).when(unityService).getWarranty(Mockito.any());

        List<WarrantyViewDTO> warrantyViewDTOS = unityController.getWarranty("12345");

        Assertions.assertEquals(warrantyViewDTO.getUnityId(), warrantyViewDTOS.get(0).getUnityId());
    }

    @Test
    void getTickets() throws UnityNotFoundException {
        RepairTicketDTO ticketDTO = new EasyRandom().nextObject(RepairTicketDTO.class);
        Mockito.doReturn(List.of(ticketDTO)).when(unityService).getTickets(Mockito.any());

        List<RepairTicketDTO> warrantyViewDTOS = unityController.getTickets("12345");

        Assertions.assertEquals(ticketDTO.getUnityId(), warrantyViewDTOS.get(0).getUnityId());
    }

    @Test
    void generalAcceptance() throws UnityNotFoundException, UnityException {
        UnityAcceptanceDTO unityAcceptanceDTO = new EasyRandom().nextObject(UnityAcceptanceDTO.class);
        MessageResponseDTO expectedResponseDTO =  MessageResponseDTO.builder().message("Teste").status(HttpStatus.OK).build();
        Mockito.doReturn(expectedResponseDTO).when(unityService).generalAcceptance(unityAcceptanceDTO);

        MessageResponseDTO responseDTO = unityController.generalAcceptance(unityAcceptanceDTO);
        Assertions.assertEquals(expectedResponseDTO.getStatus(), responseDTO.getStatus());
    }

    @Test
    void composition() {
        CompositionUnityDTO compositionUnityDTO = new EasyRandom(). nextObject(CompositionUnityDTO.class);
        Mockito.doReturn(compositionUnityDTO).when(compositionService).getComposition(Mockito.any());

        CompositionUnityDTO returnedComposition = unityController.composition("1");
        Assertions.assertEquals(compositionUnityDTO.getUnityModel(), returnedComposition.getUnityModel());
    }

    @Test
    void addComposition() {
        CompositionUnityDTO compositionUnityDTO = new EasyRandom(). nextObject(CompositionUnityDTO.class);
        Mockito.doReturn(compositionUnityDTO).when(compositionService).addComposition(Mockito.any(), Mockito.anyString());

        CompositionUnityDTO returnedComposition = unityController.addComposition("1", "2");
        Assertions.assertEquals(compositionUnityDTO.getUnityModel(), returnedComposition.getUnityModel());
    }

    @Test
    void removeComposition() {
        CompositionUnityDTO compositionUnityDTO = new EasyRandom(). nextObject(CompositionUnityDTO.class);
        Mockito.doReturn(compositionUnityDTO).when(compositionService).removeComposition(Mockito.any());

        CompositionUnityDTO returnedComposition = unityController.removeComposition(compositionUnityDTO);
        Assertions.assertEquals(compositionUnityDTO.getUnityModel(), returnedComposition.getUnityModel());
    }

    @Test
    void compositionReport() {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(compositionService).report(Mockito.any());

        CompositionUnityDTO compositionUnityDTO = new EasyRandom().nextObject(CompositionUnityDTO.class);
        ResponseEntity<byte[]> responseReposrt = unityController.compositionReport(compositionUnityDTO);

        assertEquals(HttpStatus.OK, responseReposrt.getStatusCode());
        assertEquals(report, responseReposrt.getBody());
    }

    @Test
    void changeBarcode() {
        ChangeBarcodeDTO changeBarcodeDTO = new EasyRandom().nextObject(ChangeBarcodeDTO.class);
        MessageResponseDTO expectedResponseDTO =  MessageResponseDTO.builder().message("Teste").status(HttpStatus.OK).build();
        Mockito.doReturn(expectedResponseDTO).when(unityService).changeBarcode(changeBarcodeDTO);

        MessageResponseDTO responseDTO = unityController.changeBarcode(changeBarcodeDTO);
        Assertions.assertEquals(expectedResponseDTO.getStatus(), responseDTO.getStatus());
    }

    @Test
    void trackingRecordReport() {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(unityService).trackingRecord(Mockito.any());

        UnityDTO unity = new EasyRandom().nextObject(UnityDTO.class);
        ResponseEntity<byte[]> responseReport = unityController.trackingRecordReport(List.of(unity));

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }

    @Test
    void unitExtractionReport() {
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(unityService).unitExtractionReport(Mockito.any());
        UnitExtractionDTO unitExtractionDTO = new EasyRandom().nextObject(UnitExtractionDTO.class);
        ResponseEntity<byte[]> responseReport = unityController.unitExtractionReport(unitExtractionDTO);

        assertEquals(HttpStatus.OK, responseReport.getStatusCode());
        assertEquals(report, responseReport.getBody());
    }
}
