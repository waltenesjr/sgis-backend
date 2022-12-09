package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.Uf;
import br.com.oi.sgis.repository.UserRegisterRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import net.bytebuddy.matcher.FilterableList;
import net.sf.jasperreports.engine.JRException;
import org.jeasy.random.EasyRandom;
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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserRegisterServiceTest {

    @InjectMocks
    private UserRegisterService userRegisterService;

    @Mock
    private ReportService reportService;
    @Mock
    private UserRegisterRepository userRegisterRepository;
    @Mock
    private UfService ufService;


    @Test
    void userExtractionReport() throws JRException, IOException {
        byte[] report = new byte[50];
        List<Uf> ufs = new EasyRandom().objects(Uf.class, 5).collect(toList());
        UserExtractionDTO filterDTO = new EasyRandom().nextObject(UserExtractionDTO.class);
        UserExtractionReportDTO dto = new EasyRandom().nextObject(UserExtractionReportDTO.class);
        filterDTO.setAllCompanies(true); filterDTO.setAllUfs(true); filterDTO.setAllDepartments(true);
        Mockito.doReturn(ufs).when(ufService).listAll();
        Mockito.doReturn(List.of(dto)).when(userRegisterRepository).findForExtraction(Mockito.any());
        Mockito.doReturn(report).when(reportService).userExtractionReport(Mockito.any(), Mockito.any());

        byte[] userExtraction = userRegisterService.userExtractionReport(filterDTO);
        assertNotNull(userExtraction);
        assertEquals(report, userExtraction);
    }

    @Test
    void userExtractionReportError() throws JRException, IOException {
        UserExtractionDTO filterDTO = new EasyRandom().nextObject(UserExtractionDTO.class);
        filterDTO.setAllCompanies(false); filterDTO.setAllUfs(false); filterDTO.setAllDepartments(false);
        UserExtractionReportDTO dto = new EasyRandom().nextObject(UserExtractionReportDTO.class);
        Mockito.doReturn(List.of(dto)).when(userRegisterRepository).findForExtraction(Mockito.any());

        Mockito.doThrow(JRException.class).when(reportService).userExtractionReport(Mockito.any(), Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->userRegisterService.userExtractionReport(filterDTO));
        assertEquals(MessageUtils.ERROR_REPORT.getDescription(), e.getMessage());
    }

    @Test
    void userExtractionEmpty() {
        UserExtractionDTO filterDTO = new EasyRandom().nextObject(UserExtractionDTO.class);
        filterDTO.setAllCompanies(false); filterDTO.setAllUfs(false); filterDTO.setAllDepartments(false);
        Mockito.doReturn(List.of()).when(userRegisterRepository).findForExtraction(Mockito.any());
        Exception e = assertThrows(IllegalArgumentException.class, ()->userRegisterService.userExtractionReport(filterDTO));
        assertEquals(MessageUtils.EMPTY_REPORT.getDescription(), e.getMessage());
    }
}