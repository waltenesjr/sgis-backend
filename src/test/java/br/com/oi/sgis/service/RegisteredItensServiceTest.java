package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.dto.RegisteredItensCriteriaDTO;
import br.com.oi.sgis.dto.RegisteredItensDTO;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.util.MessageUtils;
import net.sf.jasperreports.engine.JRException;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RegisteredItensServiceTest {

    @InjectMocks
    private RegisteredItensService registeredItensService;
    @Mock
    private DepartmentService departmentService;
    @Mock
    private UnityRepository registeredItensRepository;
    @Mock
    private ReportService reportService;

    RegisteredItensCriteriaDTO criteriaDTO;

    @BeforeEach
    void setUp(){
        criteriaDTO = RegisteredItensCriteriaDTO.builder().filterByNumber(true)
                .filterByPeriod(false).responsibleId("responisible").number("1234").build();
    }

    @Test
    void registeredItens() throws DepartmentNotFoundException, JRException, IOException {
        List<RegisteredItensDTO> returnedItens = new EasyRandom().objects(RegisteredItensDTO.class, 20).collect(Collectors.toList());
        Mockito.doReturn(DepartmentDTO.builder().build()).when(departmentService).findById(Mockito.any());
        Mockito.doReturn(returnedItens).when(registeredItensRepository).findRegisteredItens(Mockito.any());
        Mockito.doReturn(new byte[50]).when(reportService).fillRegisteredItensReport(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        byte[] reportItens = registeredItensService.report(criteriaDTO, TypeDocEnum.XLSX);

        assertNotNull(reportItens);
    }

    @Test
    void registeredItensEmpty() throws DepartmentNotFoundException {
        Mockito.doReturn(DepartmentDTO.builder().build()).when(departmentService).findById(Mockito.any());
        Mockito.doReturn(List.of()).when(registeredItensRepository).findRegisteredItens(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, ()->registeredItensService.report(criteriaDTO, TypeDocEnum.XLSX));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void registeredItensInvalidPeriod() throws DepartmentNotFoundException {
        criteriaDTO.setFilterByNumber(false); criteriaDTO.setFilterByPeriod(true);
        Mockito.doReturn(DepartmentDTO.builder().build()).when(departmentService).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, ()->registeredItensService.report(criteriaDTO, TypeDocEnum.XLSX));
        assertEquals(MessageUtils.INVALID_DATE.getDescription(), e.getMessage());
    }

    @Test
    void registeredItensInvalidNumber() throws DepartmentNotFoundException {
        criteriaDTO.setNumber(null);
        Mockito.doReturn(DepartmentDTO.builder().build()).when(departmentService).findById(Mockito.any());

        Exception e = assertThrows(IllegalArgumentException.class, ()->registeredItensService.report(criteriaDTO, TypeDocEnum.XLSX));
        assertEquals(MessageUtils.INVALID_NUMBER.getDescription(), e.getMessage());
    }
}