package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.AreaEquipamentDTO;
import br.com.oi.sgis.dto.ReprocessableUnityDTO;
import br.com.oi.sgis.dto.UnityToReprocessSapDTO;
import br.com.oi.sgis.enums.InstallationReasonEnum;
import br.com.oi.sgis.enums.ReasonForWriteOffEnum;
import br.com.oi.sgis.enums.RegisterReasonEnum;
import br.com.oi.sgis.exception.AreaEquipamentNotFoundException;
import br.com.oi.sgis.exception.NotReprocessableUnityException;
import br.com.oi.sgis.util.MessageUtils;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReprocessUnityServiceTest {

    @InjectMocks
    private ReprocessUnityService service;

    @Mock
    private AreaEquipamentService areaEquipamentService;


    @Test
    void reprocessUnity() throws AreaEquipamentNotFoundException{
        ReprocessableUnityDTO reprocessableUnityDTO = new EasyRandom().nextObject(ReprocessableUnityDTO.class);
        AreaEquipamentDTO areaEquipamentDTO = AreaEquipamentDTO.builder().id("1").description("teste").mnemonic("mnemonic").build();

        Mockito.doReturn(areaEquipamentDTO).when(areaEquipamentService).findById(Mockito.any());
        reprocessableUnityDTO.setCanBeReprocessed(1);
        reprocessableUnityDTO.setOperation("I");
        reprocessableUnityDTO.setRegisterReason(RegisterReasonEnum.CUS.getReason());

        UnityToReprocessSapDTO unityToReprocessSap = assertDoesNotThrow(()->service.reprocessUnity(reprocessableUnityDTO));
        assertEquals(areaEquipamentDTO.getId(), unityToReprocessSap.getUnityCode());
        assertNull(unityToReprocessSap.getOriginUf());
    }

    @Test
    void reprocessUnitySecondCondition() throws AreaEquipamentNotFoundException{
        ReprocessableUnityDTO reprocessableUnityDTO = new EasyRandom().nextObject(ReprocessableUnityDTO.class);
        AreaEquipamentDTO areaEquipamentDTO = AreaEquipamentDTO.builder().id("1").description("teste").mnemonic("mnemonic").build();

        Mockito.doReturn(areaEquipamentDTO).when(areaEquipamentService).findById(Mockito.any());
        reprocessableUnityDTO.setCanBeReprocessed(1);
        reprocessableUnityDTO.setOperation("M");
        reprocessableUnityDTO.setRegisterReason(RegisterReasonEnum.CTR.getReason());

        UnityToReprocessSapDTO unityToReprocessSap = assertDoesNotThrow(()->service.reprocessUnity(reprocessableUnityDTO));
        assertEquals(areaEquipamentDTO.getId(), unityToReprocessSap.getUnityCode());
        assertEquals(reprocessableUnityDTO.getOriginUf(), unityToReprocessSap.getOriginUf());
    }

    @Test
    void reprocessUnityThirdCondition() throws AreaEquipamentNotFoundException {
        ReprocessableUnityDTO reprocessableUnityDTO = new EasyRandom().nextObject(ReprocessableUnityDTO.class);
        AreaEquipamentDTO areaEquipamentDTO = AreaEquipamentDTO.builder().id("1").description("teste").mnemonic("mnemonic").build();

        Mockito.doReturn(areaEquipamentDTO).when(areaEquipamentService).findById(Mockito.any());
        reprocessableUnityDTO.setCanBeReprocessed(1);
        reprocessableUnityDTO.setOperation("M");
        reprocessableUnityDTO.setRegisterReason(null);
        reprocessableUnityDTO.setReasonForWriteOff(InstallationReasonEnum.IFM.getLostReason());

        UnityToReprocessSapDTO unityToReprocessSap = assertDoesNotThrow(()->service.reprocessUnity(reprocessableUnityDTO));
        assertEquals(InstallationReasonEnum.IFM.getDescription(), unityToReprocessSap.getReason());
        assertEquals("Motivo de Instalação", unityToReprocessSap.getReasonLabel());
    }

    @Test
    void reprocessUnityThirdConditionReasonToWriteOffAsReason() throws AreaEquipamentNotFoundException {
        ReprocessableUnityDTO reprocessableUnityDTO = new EasyRandom().nextObject(ReprocessableUnityDTO.class);
        AreaEquipamentDTO areaEquipamentDTO = AreaEquipamentDTO.builder().id("1").description("teste").mnemonic("mnemonic").build();

        Mockito.doReturn(areaEquipamentDTO).when(areaEquipamentService).findById(Mockito.any());
        reprocessableUnityDTO.setCanBeReprocessed(1);
        reprocessableUnityDTO.setOperation("M");
        reprocessableUnityDTO.setRegisterReason(null);
        reprocessableUnityDTO.setReasonForWriteOff(ReasonForWriteOffEnum.OBS.getReason());

        UnityToReprocessSapDTO unityToReprocessSap = assertDoesNotThrow(()->service.reprocessUnity(reprocessableUnityDTO));
        assertEquals(ReasonForWriteOffEnum.OBS.getDescription(), unityToReprocessSap.getReason());
        assertEquals("Motivo de Baixa", unityToReprocessSap.getReasonLabel());
    }

    @Test
    void reprocessUnitySecondConditionErrorOnFoundEquipamentArea() throws AreaEquipamentNotFoundException{
        ReprocessableUnityDTO reprocessableUnityDTO = new EasyRandom().nextObject(ReprocessableUnityDTO.class);

        Mockito.doThrow(AreaEquipamentNotFoundException.class).when(areaEquipamentService).findById(Mockito.any());
        reprocessableUnityDTO.setCanBeReprocessed(1);
        reprocessableUnityDTO.setOperation("M");
        reprocessableUnityDTO.setRegisterReason(RegisterReasonEnum.CTR.getReason());

        Exception e = assertThrows(NotReprocessableUnityException.class,()->service.reprocessUnity(reprocessableUnityDTO));
        assertEquals(MessageUtils.UNITY_SAP_REPROCESS_UNITY_CODE_ERROR.getDescription(),e.getMessage());

    }

    @Test
    void filledNoneConditionToReprocess() {
        ReprocessableUnityDTO reprocessableUnityDTO = new EasyRandom().nextObject(ReprocessableUnityDTO.class);
        Exception e = assertThrows(NotReprocessableUnityException.class,()->service.reprocessUnity(reprocessableUnityDTO));
        assertEquals(MessageUtils.UNITY_REPROCESS_SAP_ERROR.getDescription(),e.getMessage());
    }

    @Test
    void isNotReprocessableByIsNull() {
        ReprocessableUnityDTO reprocessableUnityDTO = null;
        Exception e = assertThrows(NotReprocessableUnityException.class,()->service.reprocessUnity(reprocessableUnityDTO));
        assertEquals(MessageUtils.UNITY_NOT_REPROCESSABLE_ERROR.getDescription(),e.getMessage());
    }

    @Test
    void isNotReprocessableByLastOperationWasSuccessful() {
        ReprocessableUnityDTO reprocessableUnityDTO = new EasyRandom().nextObject(ReprocessableUnityDTO.class);

        reprocessableUnityDTO.setCanBeReprocessed(3);

        Exception e = assertThrows(NotReprocessableUnityException.class,()->service.reprocessUnity(reprocessableUnityDTO));
        assertEquals(MessageUtils.UNITY_SAP_SUCCESS_ERROR.getDescription(),e.getMessage());

    }
    @Test
    void isNotReprocessableByLastOperationAlreadySent() {
        ReprocessableUnityDTO reprocessableUnityDTO = new EasyRandom().nextObject(ReprocessableUnityDTO.class);
        reprocessableUnityDTO.setCanBeReprocessed(2);

        Exception e = assertThrows(NotReprocessableUnityException.class,()->service.reprocessUnity(reprocessableUnityDTO));
        assertEquals(MessageUtils.UNITY_ALREADY_REPROCESSABLE_ERROR.getDescription(),e.getMessage());

    }
}