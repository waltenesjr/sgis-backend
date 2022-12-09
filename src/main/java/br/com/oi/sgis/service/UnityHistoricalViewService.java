package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.UnityHistoricalViewDTO;
import br.com.oi.sgis.dto.UnityHistoricalViewFilterDTO;
import br.com.oi.sgis.enums.TypeDocEnum;
import br.com.oi.sgis.mapper.UnityHistoricalViewMapper;
import br.com.oi.sgis.repository.UnityHistoricalViewRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import br.com.oi.sgis.util.Utils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UnityHistoricalViewService {

    private final UnityHistoricalViewRepository unityHistoricalViewRepository;
    private final ReportService reportService;

    private static final UnityHistoricalViewMapper unityHistoricalViewMapper = UnityHistoricalViewMapper.INSTANCE;

    public PaginateResponseDTO<UnityHistoricalViewDTO> findPaginated(UnityHistoricalViewFilterDTO filter,
                                                                     Integer pageNo, Integer pageSize,
                                                                     List<String> sortAsc, List<String> sortDesc) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));

        if (filter.getInitialDate() != null || filter.getFinalDate() != null)
            Utils.isPeriodInvalid(filter.getInitialDate(), filter.getFinalDate());

        if(filter.getInitialDate() != null && filter.getFinalDate() != null)
            return PageableUtil.paginate(unityHistoricalViewRepository.findWithDate(filter.getBarcode(), filter.getInitialDate(),
                    filter.getFinalDate(), filter.getFromResponsible(), filter.getFromDeposit(),
                    filter.getFromTechnician(), paging).map(unityHistoricalViewMapper::toDTO));

        return PageableUtil.paginate(unityHistoricalViewRepository.find(filter.getBarcode(), filter.getFromResponsible(), filter.getFromDeposit(),
                        filter.getFromTechnician(), paging).map(unityHistoricalViewMapper::toDTO));
    }

    public byte[] unityHistoricalReport(UnityHistoricalViewFilterDTO filter, List<String> sortAsc, List<String> sortDesc,
                                        TypeDocEnum typeDocEnum){
        List<UnityHistoricalViewDTO> data = findPaginated(filter, 0, Integer.MAX_VALUE, sortAsc, sortDesc).getData();
        if (data.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        try {
            return reportService.unityHistoricalReport(data, typeDocEnum);
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }
}
