package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.exception.DepartmentNotFoundException;
import br.com.oi.sgis.repository.UnityRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ItensInstallByStealReasonServiceTest {

    @InjectMocks
    private ItensInstallByStealReasonService service;
    @Mock
    private UnityRepository itensByStealReasonRepository;
    @Mock
    private DepartmentService departmentService;
    @Mock
    private ReportService reportService;

    private   ItensInstallByStealReasonCriteriaDTO criteriaDTO;

    @BeforeEach
    void setUp(){
        criteriaDTO = ItensInstallByStealReasonCriteriaDTO.builder()
                .installationReason(List.of(InstallationReasonDTO.builder().cod("IFD").build()))
                .finalPeriod(LocalDateTime.now())
                .initialPeriod(LocalDateTime.now().minusDays(5))
                .responsibleCode("Code")
                .build();
    }

    @Test
    void listItens() throws DepartmentNotFoundException {
        List<ItensInstallByStealReasonDTO> itensInstallByStealReasonDTOS = new EasyRandom().objects(ItensInstallByStealReasonDTO.class, 20).collect(Collectors.toList());
        itensInstallByStealReasonDTOS.forEach(x -> x.setReason("IFD"));
        DepartmentDTO departmentDTO = DepartmentDTO.builder().build();
        Mockito.doReturn(departmentDTO).when(departmentService).findById(Mockito.any());
        Mockito.doReturn(itensInstallByStealReasonDTOS).when(itensByStealReasonRepository).findByParamsSteal(Mockito.any());

        List<ItensInstallByStealReasonReportDTO> response = service.listItens(criteriaDTO);
        assertNotNull(response);
        assertEquals(itensInstallByStealReasonDTOS.size(), response.get(0).getItens().size());
    }

    @Test
    void report() throws DepartmentNotFoundException, JRException, IOException {
        List<ItensInstallByStealReasonDTO> itensInstallByStealReasonDTOS = new EasyRandom().objects(ItensInstallByStealReasonDTO.class, 20).collect(Collectors.toList());
        itensInstallByStealReasonDTOS.forEach(x -> x.setReason("IFD"));
        DepartmentDTO departmentDTO = DepartmentDTO.builder().build();
        Mockito.doReturn(departmentDTO).when(departmentService).findById(Mockito.any());
        Mockito.doReturn(itensInstallByStealReasonDTOS).when(itensByStealReasonRepository).findByParamsSteal(Mockito.any());
        Mockito.doReturn(new byte[50]).when(reportService).fillItensBySteal(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        byte[] response = service.report(criteriaDTO, TypeDocEnum.XLSX);

        assertNotNull(response);
    }
}