package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.repository.ProceduresRepository;
import br.com.oi.sgis.service.validator.Validator;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class NasphServiceTest {
    @InjectMocks
    private NasphService nasphService;
    @Mock
    private Validator<Object> validator;
    @Mock
    private ProceduresRepository repository;


    @Test
    void transfProperty(){
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.transfProperty(unityDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }


    @Test
    void repoProperty(){
        UnityDTO unityDTO = new EasyRandom().nextObject(UnityDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.repoProperty(unityDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void updateUnitySituation(){
        UnitySituationDTO unitySituationDTO = new EasyRandom().nextObject(UnitySituationDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.updateUnitySituation(unitySituationDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void updateUnitySwap(){
        UnitySwapDTO unitySwapDTO = new EasyRandom().nextObject(UnitySwapDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.updateUnitySwap(unitySwapDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void updatePlanInstallation(){
        PlanInstallationDTO planInstallationDTO = new EasyRandom().nextObject(PlanInstallationDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.updatePlanInstallation(planInstallationDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void updateUnityWriteOff(){
        UnityWriteOffDTO unityWriteOffDTO = new EasyRandom().nextObject(UnityWriteOffDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.updateUnityWriteOff(unityWriteOffDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void recoverItem(){
        RecoverItemDTO recoverItemDTO = new EasyRandom().nextObject(RecoverItemDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.recoverItem(recoverItemDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void reprocessSapUnity() {
        ReprocessSapDTO reprocessSapDTO = new EasyRandom().nextObject(ReprocessSapDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.reprocessSapUnity(reprocessSapDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void acceptRepairTicket() {
        AcceptTcktRepairNasphDTO acceptTicketRepairDTO = new EasyRandom().nextObject(AcceptTcktRepairNasphDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.acceptRepairTicket(acceptTicketRepairDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void generalAcceptance() {
        UnityAcceptanceDTO unityAcceptanceDTO = new EasyRandom().nextObject(UnityAcceptanceDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.generalAcceptance(unityAcceptanceDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void closeRepair() {
        CloseRepairTickectDTO closeRepairTickectDTO = new EasyRandom().nextObject(CloseRepairTickectDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.closeRepair(closeRepairTickectDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void closeRepairSituation() {
        Mockito.doReturn("Teste").when(repository).finalSituationRepair(any());
        String situationDTO = nasphService.closeRepairSituation("123");
        assertNotNull(situationDTO);
    }

    @Test
    void devolutionRepair() {
        DevolutionRepairTicketDTO devolutionRepairTicketDTO = new EasyRandom().nextObject(DevolutionRepairTicketDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.devolutionRepair(devolutionRepairTicketDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void cancelRepair() {
        MessageResponseDTO messageResponseDTO = nasphService.cancelRepair("1", "123");
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void transferTechnicalStaff() {
        TransferTechnicalDTO transferTechnicalDTO = new EasyRandom().nextObject(TransferTechnicalDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.transferTechnicalStaff(transferTechnicalDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void transferProvider() {
        TransferProviderDTO transferProviderDTO = new EasyRandom().nextObject(TransferProviderDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.transferProvider(transferProviderDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void instClientByProvider() {
        ClientInstByProviderDTO clientInstByProviderDTO = new EasyRandom().nextObject(ClientInstByProviderDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.instClientByProvider(clientInstByProviderDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void instClientByTechnician() {
        ClientInstByProviderDTO clientInstByProviderDTO = new EasyRandom().nextObject(ClientInstByProviderDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.instClientByTechnician(clientInstByProviderDTO, "1", "2");
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void changeBarcode() {
        ChangeBarcodeDTO changeBarcodeDTO = new EasyRandom().nextObject(ChangeBarcodeDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.changeBarcode(changeBarcodeDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void updateInterventionSituation() {
        Mockito.doReturn("Teste").when(repository).interventionSituation(any());
        String situationDTO = nasphService.updateInterventionSituation("123");
        assertNotNull(situationDTO);
    }

    @Test
    void estimateExternalOutput() {
        EstimateExternalOutputDTO estimateExternalOutputDTO = new EasyRandom().nextObject(EstimateExternalOutputDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.estimateExternalOutput(estimateExternalOutputDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void cancelItemEstimate() {
        assertDoesNotThrow(() ->nasphService.cancelItemEstimate("123"));
    }

    @Test
    void approveItemEstimate() {
        assertDoesNotThrow(() ->nasphService.approveItemEstimate("123", "123", BigDecimal.ONE));
    }

    @Test
    void cancelEstimate() {
        MessageResponseDTO messageResponseDTO = nasphService.cancelEstimate("111");
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }

    @Test
    void returnExternalRepair() {
        RepairExternalReturnDTO repairExternalReturnDTO = new EasyRandom().nextObject(RepairExternalReturnDTO.class);
        MessageResponseDTO messageResponseDTO = nasphService.returnExternalRepair(repairExternalReturnDTO);
        Assertions.assertEquals(HttpStatus.OK, messageResponseDTO.getStatus());
    }
}