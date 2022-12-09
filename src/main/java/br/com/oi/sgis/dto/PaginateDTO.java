package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder @AllArgsConstructor
public class PaginateDTO {
    private long totalItens;
    private int totalPages;
    private int numberOfItens;
    private int activePage;
    private List<SortDTO> sorts;
}
