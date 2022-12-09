package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.DepartmentComponentDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.mapper.DepartmentComponentMapper;
import br.com.oi.sgis.repository.DepartmentComponentRepository;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import br.com.oi.sgis.util.Utils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DepartmentComponentService {

    private final DepartmentComponentRepository departmentComponentRepository;

    private static final DepartmentComponentMapper departmentComponentMapper = DepartmentComponentMapper.INSTANCE;

    public PaginateResponseDTO<DepartmentComponentDTO> listByDeparmentUserPaginated (Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc){
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap = DepartmentComponentMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc, sortMap));
        String departmentId = Utils.getUser().getDepartmentCode().getId();

        return PageableUtil.paginate(departmentComponentRepository.findByDepartment(departmentId, paging).map(departmentComponentMapper::toDTO), sortMap);
    }
}
