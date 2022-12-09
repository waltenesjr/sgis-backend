package br.com.oi.sgis.util;

import br.com.oi.sgis.dto.PaginateDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.SortDTO;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PageableUtil {

    private PageableUtil(){}

    public static <T> PaginateResponseDTO <T> paginate(Page<T> page){
        List<SortDTO> sorts = page.getSort().get().map(SortDTO::new).collect(Collectors.toList());
        return buildPaginateDTO(page, sorts);

    }

    private static  <T>  PaginateResponseDTO <T> buildPaginateDTO(Page<T> page, List<SortDTO> sorts) {
        PaginateDTO paginate = PaginateDTO.builder().numberOfItens(page.getPageable().getPageSize())
                .totalItens(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .activePage(page.getPageable().getPageNumber() + 1)
                .sorts(sorts)
                .build();

        PaginateResponseDTO<T> paginated = new PaginateResponseDTO<>();
        paginated.setData(page.getContent());
        paginated.setPaginate(paginate);
        return paginated;
    }

    public static <T> PaginateResponseDTO<T> paginate(Page<T> page, Map<String, String> sortMap){
        List<SortDTO> sorts = page.getSort().get().map(SortDTO::new).collect(Collectors.toList());
        verifySortsTerms(sorts, sortMap);
        return buildPaginateDTO(page, sorts);

    }

    private static void verifySortsTerms(List<SortDTO> sortList, Map<String, String> map) {
        BiMap<String, String> biMap = HashBiMap.create(map).inverse();
        if(sortList!=null)
            sortList.forEach(m-> m.setProperty(getProperty(biMap, m)));
    }

    private static String getProperty(BiMap<String, String> biMap, SortDTO m) {
        return biMap.get(m.getProperty())!=null? biMap.get(m.getProperty()): m.getProperty();
    }

    public static int correctPageNo(int pageNo){
        return pageNo<=0 ? 0 : pageNo-1;
    }

}
