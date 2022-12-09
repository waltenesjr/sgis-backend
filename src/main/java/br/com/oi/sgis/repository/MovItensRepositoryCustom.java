package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.MovItensReportDTO;
import br.com.oi.sgis.entity.view.MovItensView;

import java.util.List;

public interface MovItensRepositoryCustom {
    List<MovItensView> findByParameters(MovItensReportDTO movItensReportDTO);
}
