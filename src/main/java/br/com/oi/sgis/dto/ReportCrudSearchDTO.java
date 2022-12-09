package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder @Data
public class ReportCrudSearchDTO {
    private String search;
    private List<String> sortAsc;
    private List<String> sortDesc;

    public String getSearch() {
        if(search==null)
            return "";
        return search;
    }

    public List<String> getSortAsc() {
        if(sortAsc == null)
            return List.of();
        return sortAsc;
    }

    public List<String> getSortDesc() {
        if(sortDesc == null)
            return List.of();
        return sortDesc;
    }
}
