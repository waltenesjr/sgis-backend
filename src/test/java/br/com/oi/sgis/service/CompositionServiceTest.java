package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.CompositionUnityDTO;
import br.com.oi.sgis.dto.CompositionUnityReportDTO;
import br.com.oi.sgis.entity.Department;
import br.com.oi.sgis.entity.Situation;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.Utils;
import net.sf.jasperreports.engine.JRException;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;
import org.hibernate.tool.schema.ast.SqlScriptParserException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class CompositionServiceTest {

    @InjectMocks
    private CompositionService compositionService;

    @Mock
    private UnityRepository unityRepository;

    @Mock
    private ReportService reportService;

    private Unity unity;

    @BeforeEach
    void setUp(){
        unity = new EasyRandom().nextObject(Unity.class);
        unity.setDeposit(Department.builder().id(Utils.getUser().getDepartmentCode().getId()).build());
        unity.setSituationCode(Situation.builder().id("DIS").build());
        unity.setBarcodeParent(null);
    }

    @Test
    void getComposition() {

        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Mockito.doReturn(List.of(unity)).when(unityRepository).findUnitiesComposition(Mockito.any());

        CompositionUnityDTO returnedComposition = compositionService.getComposition("1");
        assertEquals("1", returnedComposition.getUnityModel());
        assertEquals(1, returnedComposition.getUnitiesItem().size());
        assertEquals(0, returnedComposition.getUnitiesItemToRemove().size());

    }

    @Test
    void addComposition() {
        Unity unityItem = new EasyRandom().nextObject(Unity.class);
        unityItem.setDeposit(Department.builder().id(Utils.getUser().getDepartmentCode().getId()).build());
        unityItem.setSituationCode(Situation.builder().id("DIS").build());
        unityItem.setBarcodeParent(null);
        unityItem.setId("2");

        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Mockito.doReturn(List.of(unityItem)).when(unityRepository).findUnitiesComposition(Mockito.any());

        CompositionUnityDTO returnedComposition = compositionService.addComposition(unity.getId(), unityItem.getId());
        assertEquals(unity.getId(), returnedComposition.getUnityModel());
        assertEquals(1, returnedComposition.getUnitiesItem().size());
        assertEquals(0, returnedComposition.getUnitiesItemToRemove().size());
    }

    @Test
    void removeComposition() {
        CompositionUnityDTO compositionUnityDTO = new EasyRandom().nextObject(CompositionUnityDTO.class);
        Mockito.doReturn(List.of(unity)).when(unityRepository).findUnitiesComposition(Mockito.any());

        CompositionUnityDTO returnedComposition = compositionService.removeComposition(compositionUnityDTO);
        assertEquals(compositionUnityDTO.getUnityModel(), returnedComposition.getUnityModel());
        assertEquals(1, returnedComposition.getUnitiesItem().size());
        assertEquals(0, returnedComposition.getUnitiesItemToRemove().size());

    }

    @Test
    void getCompositionInvalid() {
        unity.setBarcodeParent("parent");
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> compositionService.getComposition("1"));
        assertEquals(MessageUtils.ITEM_NOT_MODEL.getDescription(), e.getMessage());
    }

    @Test
    void addCompositionNotValidDepartment() {
        Unity unityItem = new EasyRandom().nextObject(Unity.class);
        unityItem.setSituationCode(Situation.builder().id("DIS").build());
        unityItem.setBarcodeParent(null);
        unityItem.setId("2");

        Mockito.doReturn(Optional.of(unityItem)).when(unityRepository).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, () -> compositionService.addComposition("1", "2"));
        assertEquals(MessageUtils.ITEM_DIFFERENT_DEP.getDescription(), e.getMessage());

    }

    @Test
    void addCompositionNotValidBarcode() {
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> compositionService.addComposition("1", "1"));
        assertEquals(MessageUtils.ITEM_INVALID_BARCODE.getDescription(), e.getMessage());
    }

    @Test
    void addCompositionError() {
        Unity unityItem = new EasyRandom().nextObject(Unity.class);
        unityItem.setSituationCode(Situation.builder().id("DIS").build());
        unityItem.setBarcodeParent(null);
        unityItem.setId("2");
        Mockito.doThrow(SqlScriptParserException.class).when(unityRepository).updateComposition(Mockito.any(), Mockito.anyString(), Mockito.any());
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> compositionService.addComposition("1", "2"));
        assertEquals(MessageUtils.UNITY_COMPOSITION_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void removeCompositionEmpty() {
        CompositionUnityDTO compositionUnityDTO = new EasyRandom().nextObject(CompositionUnityDTO.class);
        compositionUnityDTO.setUnitiesItemToRemove(List.of());
        Exception e = assertThrows(IllegalArgumentException.class, () -> compositionService.removeComposition(compositionUnityDTO));
        assertEquals(MessageUtils.UNITY_REMOVE_NO_ITENS_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void removeCompositionError() {
        CompositionUnityDTO compositionUnityDTO = new EasyRandom().nextObject(CompositionUnityDTO.class);
        Mockito.doThrow(SqlScriptParserException.class).when(unityRepository).updateComposition(Mockito.any(), Mockito.anyString(), Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, () -> compositionService.removeComposition(compositionUnityDTO));
        assertEquals(MessageUtils.UNITY_REMOVE_COMPOSITION_ERROR.getDescription(), e.getMessage());
    }

    @Test
    void report() throws JRException, IOException, OutputException, BarcodeException {
        CompositionUnityDTO compositionUnityDTO = new EasyRandom().nextObject(CompositionUnityDTO.class);
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(Mockito.any());
        byte[] report = new byte[50];
        Mockito.doReturn(report).when(reportService).report(Mockito.any(CompositionUnityReportDTO.class));
        byte[] source = compositionService.report(compositionUnityDTO);
        assertNotNull(source);
    }

    @Test
    void reportException() {
        String idItem = "item";
        String idModel = "model";
        CompositionUnityDTO compositionUnityDTO = new EasyRandom().nextObject(CompositionUnityDTO.class);
        compositionUnityDTO.getUnitiesItem().forEach(x -> x.setId(idItem));
        compositionUnityDTO.setUnityModel(idModel);
        Mockito.doReturn(Optional.of(unity)).when(unityRepository).findById(idModel);
        Mockito.doReturn(Optional.empty()).when(unityRepository).findById(idItem);
        Exception e = assertThrows(IllegalArgumentException.class, () -> compositionService.report(compositionUnityDTO));
        assertEquals(MessageUtils.UNITY_NOT_FOUND_BY_ID.getDescription() + idItem, e.getMessage());
    }
}