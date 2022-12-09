package br.com.oi.sgis.dto;

import br.com.oi.sgis.entity.Level;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;


@ExtendWith(MockitoExtension.class)
class TechnicalStaffDTOTest {

    @Test
    void technicalStaffDTO(){
        TechnicalStaffDTO technicalStaffDTO = new TechnicalStaffDTO();
        technicalStaffDTO.setId("1L");
        technicalStaffDTO.setActive(true);
        technicalStaffDTO.setTechnicianName("João");
        technicalStaffDTO.setEmail("joao@email.com");
        technicalStaffDTO.setLevels(Set.of(Level.builder().id("1").build()));
        technicalStaffDTO.setRepairFlag(false);
        technicalStaffDTO.setCgcCpfCompany(ParameterDTO.builder().id("1").build());
        technicalStaffDTO.setManagerName("José");

        Assertions.assertNull(technicalStaffDTO.getDepartmentCode());
        Assertions.assertEquals("1L", technicalStaffDTO.getId());
        Assertions.assertTrue(technicalStaffDTO.isActive());
        Assertions.assertEquals("João", technicalStaffDTO.getTechnicianName());
        Assertions.assertEquals("joao@email.com", technicalStaffDTO.getEmail());
        Assertions.assertEquals(1, technicalStaffDTO.getLevels().size());
        Assertions.assertEquals("José", technicalStaffDTO.getManagerName());
        Assertions.assertNotNull(technicalStaffDTO.getCgcCpfCompany());
        Assertions.assertFalse(technicalStaffDTO.isRepairFlag());
        Assertions.assertNull(technicalStaffDTO.getTechPhoneBase());
        Assertions.assertNull(technicalStaffDTO.getTechPhoneResid());
    }
}