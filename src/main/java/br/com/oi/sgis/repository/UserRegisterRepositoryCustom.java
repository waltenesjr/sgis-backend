package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.UserExtractionDTO;
import br.com.oi.sgis.dto.UserExtractionReportDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRegisterRepositoryCustom {

    List<UserExtractionReportDTO> findForExtraction(UserExtractionDTO userExtractionDTO);
}
