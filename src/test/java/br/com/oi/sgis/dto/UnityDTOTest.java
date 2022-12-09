package br.com.oi.sgis.dto;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnityDTOTest {

    @Test
    void unityDTOTest() {
        DepartmentDTO departmentDTO = new DepartmentDTO();
        departmentDTO.setId("12345D");
        AreaEquipamentDTO areaEquipamentDTO = AreaEquipamentDTO.builder().id("12345").build();
        TechnicalStaffDTO technicalStaffDTO = TechnicalStaffDTO.builder().id("12345").build();

        UnityDTO unityDTO = UnityDTO.builder().id("123")
                .serieNumber("serie")
                .orderNumber("order")
                .orderItem("0")
                .fixedNumber("12347487")
                .responsible(departmentDTO)
                .deposit(departmentDTO)
                .unityCode(areaEquipamentDTO)
                .technician(technicalStaffDTO)
                .reservationItem("1234")
                .fixedSubnumber("1456")
                .instalationReason("Install")
                .originUf("MA")
                .fixedSubnumber("457952")
                .instalationReason("IPI")
                .build();

        Assertions.assertEquals("123", unityDTO.getId());
        Assertions.assertEquals("12347487", unityDTO.getFixedNumber());

    }
}
