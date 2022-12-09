package br.com.oi.sgis.service.factory.impl;

import br.com.oi.sgis.dto.AreaEquipamentDTO;
import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.dto.TechnicalStaffDTO;
import br.com.oi.sgis.dto.UnityDTO;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.enums.RegisterReasonEnum;
import br.com.oi.sgis.enums.SituationEnum;
import br.com.oi.sgis.enums.TechiniqueCodeEnum;
import br.com.oi.sgis.exception.AreaEquipamentNotFoundException;
import br.com.oi.sgis.exception.TechnicalStaffNotFoundException;
import br.com.oi.sgis.exception.UnityException;
import br.com.oi.sgis.mapper.UnityMapper;
import br.com.oi.sgis.service.AreaEquipamentService;
import br.com.oi.sgis.service.TechnicalStaffService;
import br.com.oi.sgis.service.validator.impl.UnityValidator;
import lombok.SneakyThrows;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;



@ExtendWith(MockitoExtension.class)
class UnityFactoryImplTest {

    @InjectMocks
    private UnityFactoryImpl unityFactory;

    @Mock
    private UnityValidator validator;

    @Mock
    private AreaEquipamentService areaEquipamentService;

    @Mock
    private TechnicalStaffService technicalStaffService;

    @MockBean
    private UnityMapper unityMapper = UnityMapper.INSTANCE;


    private UnityDTO unityDTO;
    @BeforeEach
    void setUp(){
        unityDTO = UnityDTO.builder().id("12345").build();
        AreaEquipamentDTO areaEquipamentDTO = new EasyRandom().nextObject(AreaEquipamentDTO.class);
        DepartmentDTO departmentDTO = new EasyRandom().nextObject(DepartmentDTO.class);
        DepartmentDTO departmentTech = new EasyRandom().nextObject(DepartmentDTO.class);
        unityDTO.setUnityCode(areaEquipamentDTO);
        unityDTO.setResponsible(departmentDTO);
        unityDTO.setTechnician(TechnicalStaffDTO.builder().id("1")
                .departmentCode(departmentTech).build());
    }

    @Test @SneakyThrows
    void shouldCreateNewSpareUnity() {
        Unity unity = unityMapper.toModel(unityDTO);
        TechnicalStaffDTO technicalStaffDTO= new EasyRandom().nextObject(TechnicalStaffDTO.class);
        technicalStaffDTO.setDepartmentCode(unityDTO.getResponsible());

        Mockito.doNothing().when(validator).validate(Mockito.any());
        Mockito.doReturn(unityDTO.getUnityCode()).when(areaEquipamentService).findById(Mockito.any());
        Mockito.doReturn(technicalStaffDTO).when(technicalStaffService).findById(Mockito.any());

        Unity unityReturn = unityFactory.makeNewSpareUnity(unityDTO);

        Assertions.assertEquals(unity.getId(), unityReturn.getId());
        Assertions.assertEquals(RegisterReasonEnum.CUS.getReason(), unityReturn.getRegisterReason());
    }

    @Test @SneakyThrows
    void shouldCreateNewSpareUnitySituationTRN()  {

        unityDTO.getTechnician().getDepartmentCode().setId("2");
        Unity unity = unityMapper.toModel(unityDTO);
        TechnicalStaffDTO technicalStaffDTO= new EasyRandom().nextObject(TechnicalStaffDTO.class);
        DepartmentDTO departmentDTO = new EasyRandom().nextObject(DepartmentDTO.class);
        departmentDTO.setId("4567667");
        technicalStaffDTO.setDepartmentCode(departmentDTO);

        Mockito.doNothing().when(validator).validate(Mockito.any());
        Mockito.doReturn(unityDTO.getUnityCode()).when(areaEquipamentService).findById(Mockito.any());
        Mockito.doReturn(technicalStaffDTO).when(technicalStaffService).findById(Mockito.any());

        Unity unityReturn = unityFactory.makeNewSpareUnity(unityDTO);

        Assertions.assertEquals(unity.getId(), unityReturn.getId());
        Assertions.assertEquals(RegisterReasonEnum.CUS.getReason(), unityReturn.getRegisterReason());
        Assertions.assertEquals(SituationEnum.TRN.getCod(), unityReturn.getSituationCode().getId());
    }

    @Test
    void shouldCreateRemovedFromSiteUnity() throws AreaEquipamentNotFoundException {
        Unity unity = unityMapper.toModel(unityDTO);
        AreaEquipamentDTO areaEquipamentDTO =AreaEquipamentDTO.builder().id("2L").techniqueCode(TechiniqueCodeEnum.INST.getCod()).build();

        Mockito.doNothing().when(validator).validate(Mockito.any());
        Mockito.doReturn(areaEquipamentDTO).when(areaEquipamentService).findById(Mockito.any());

        Unity unityReturn = unityFactory.makeRemovedFromSite(unityDTO);

        Assertions.assertEquals(unity.getId(), unityReturn.getId());
        Assertions.assertEquals(RegisterReasonEnum.CRP.getReason(), unityReturn.getRegisterReason());

    }

    @Test
    void shouldCreateUnityCUSThowsUnityExceptionRequiredDatas() throws AreaEquipamentNotFoundException {
        Mockito.doReturn(null).when(areaEquipamentService).findById(Mockito.any());

        Assertions.assertThrows(UnityException.class, ()-> unityFactory.makeNewSpareUnity(unityDTO));
    }
    @Test
    void shouldCreateUnityCPRThowsUnityExceptionRequiredDatas() throws AreaEquipamentNotFoundException {
        Mockito.doReturn(null).when(areaEquipamentService).findById(Mockito.any());

        Assertions.assertThrows(UnityException.class, ()-> unityFactory.makeRemovedFromSite(unityDTO));
    }

    @Test @SneakyThrows
    void shouldThrowExceptionTechnicianNotFound() {

        Mockito.doReturn(unityDTO.getUnityCode()).when(areaEquipamentService).findById(Mockito.any());
        Mockito.doThrow(TechnicalStaffNotFoundException.class).when(technicalStaffService).findById(Mockito.any());

        Assertions.assertThrows(TechnicalStaffNotFoundException.class, () ->  unityFactory.makeNewSpareUnity(unityDTO));
    }

}