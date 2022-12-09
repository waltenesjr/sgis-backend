package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.SapOperationHistoryDTO;
import br.com.oi.sgis.dto.SapOperationHistoryReportDTO;
import br.com.oi.sgis.repository.InformaticsRecRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class InformaticsRecService {

    private final InformaticsRecRepository informaticsRecRepository;
    public PaginateResponseDTO<SapOperationHistoryReportDTO> getSapOperationHistory(SapOperationHistoryDTO dto, Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        Assert.isTrue(dto.getFinalDate().isAfter(dto.getInitialDate()), MessageUtils.INVALID_PERIOD.getDescription());
        if (dto.getSearch() == null)
            dto.setSearch("");

        return PageableUtil.paginate(informaticsRecRepository.findSapOperationHistory(dto, paging));
    }
}
