package br.com.oi.sgis.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DepartmentDTOTest {

    @Test
    void departmentDTOTest(){
        DepartmentDTO departmentDTO = new DepartmentDTO();
        departmentDTO.setId("1232");
        departmentDTO.setDescription("Description");
        departmentDTO.setCostCenter("Cost Center");
        departmentDTO.setObligateUse(false);
        departmentDTO.setInactive(false);
        departmentDTO.setSystemFlag(true);
        departmentDTO.setSpareCenter(true);
        departmentDTO.setPhone("9899991545");
        departmentDTO.setRepairCenterDefault("Repair Center");
        departmentDTO.setDirectorship("Diretoria");
        departmentDTO.setManagerName("Joao");
        departmentDTO.setContactName("Jose");

        assertEquals("1232",departmentDTO.getId());
        assertEquals("Description",departmentDTO.getDescription());
        assertEquals("Cost Center",departmentDTO.getCostCenter());
        assertEquals("9899991545",departmentDTO.getPhone());
        assertEquals("Repair Center",departmentDTO.getRepairCenterDefault());
        assertEquals("Diretoria",departmentDTO.getDirectorship());
        assertEquals("Joao",departmentDTO.getManagerName());
        assertEquals("Jose",departmentDTO.getContactName());
        assertTrue(departmentDTO.isSystemFlag());
        assertTrue(departmentDTO.isSpareCenter());
        assertFalse(departmentDTO.isInactive());
        assertFalse(departmentDTO.isObligateUse());
        assertNull(departmentDTO.getAddress());
        assertNull(departmentDTO.getManagerRegisterNum());
        assertNull(departmentDTO.getContactRegisterNum());
    }
}