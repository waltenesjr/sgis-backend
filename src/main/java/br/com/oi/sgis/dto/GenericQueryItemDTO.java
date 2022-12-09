package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericQueryItemDTO {
    private Long genericQueryId;
    private Long columnSequence;
    private String columnName;
    private String valueOne;
    private String valueTwo;
    private String operator;
    private boolean flagNonull;
    private boolean flagNulls;
    private boolean noShow;
    private boolean flagOrder;

}
