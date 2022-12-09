package br.com.oi.sgis.util;

import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SortUtil {

    private SortUtil() {
    }

    public static Sort createSorts(List<String> sortAsc, List<String> sortDesc) {
        return getOrders(sortAsc, sortDesc);
    }

    public static Sort createSorts(List<String> sortAsc, List<String> sortDesc, Map<String, String> mapValues) {
        sortAsc = verifySortsTerms(sortAsc,mapValues);
        sortDesc = verifySortsTerms(sortDesc,mapValues);
        return getOrders(sortAsc, sortDesc);
    }

    private static Sort getOrders(List<String> sortAsc, List<String> sortDesc) {
        List<Sort.Order> sortOrders = new ArrayList<>();
        if(sortAsc != null)
            sortOrders.addAll(sortAsc.parallelStream().map(Sort.Order::asc).collect(Collectors.toList()));
        if(sortDesc !=null)
            sortOrders.addAll(sortDesc.parallelStream().map(Sort.Order::desc).collect(Collectors.toList()));
        return Sort.by(sortOrders);
    }

    private static List<String> verifySortsTerms(List<String> sortList, Map<String, String> map) {
        if(sortList!=null)
            return sortList.stream()
                    .map(m-> map.containsKey(m) ?
                            m.replace(m, map.get(m)):m)
                    .collect(Collectors.toList());
        return sortList;
    }


    
}
