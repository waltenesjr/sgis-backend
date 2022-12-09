package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Builder @Data
public class UserExtractionDTO {
    private List<String> ufs;
    private List<String> companies;
    private List<String> departments;
    private boolean allUfs;
    private boolean allCompanies;
    private boolean allDepartments;

    public List<String> getUfs() {
        if(ufs==null)
            return Collections.emptyList();
        return ufs;
    }
    public List<String> getCompanies() {
        if(companies==null )
            return Collections.emptyList();
        return companies;
    }

    public List<String> getDepartments() {
        if(departments ==null )
            return Collections.emptyList();
        return departments;
    }
}
