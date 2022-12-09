package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.CompanyDTO;
import br.com.oi.sgis.dto.DepartmentDTO;
import br.com.oi.sgis.dto.UserExtractionDTO;
import br.com.oi.sgis.dto.UserExtractionReportDTO;
import br.com.oi.sgis.entity.Department;
import br.com.oi.sgis.entity.Uf;
import br.com.oi.sgis.repository.DepartmentRepository;
import br.com.oi.sgis.repository.UserRegisterRepository;
import br.com.oi.sgis.util.MessageUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserRegisterService {

    private final UserRegisterRepository userRegisterRepository;
    private final ReportService reportService;
    private final UfService ufService;

    public byte[] userExtractionReport(UserExtractionDTO userExtractionDTO) {
        List<String> ufs = userExtractionDTO.isAllUfs() ? getAllUfs() : userExtractionDTO.getUfs();
        userExtractionDTO.setUfs(ufs);
        List<UserExtractionReportDTO> usersExtraction = userRegisterRepository.findForExtraction(userExtractionDTO);

        if(usersExtraction.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        Map<String, Object> parameters = new HashMap<>();
        try {
            return reportService.userExtractionReport(usersExtraction, parameters);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

    private List<String> getAllUfs() {
        return ufService.listAll().stream().map(Uf::getId).collect(toList());
    }

}
