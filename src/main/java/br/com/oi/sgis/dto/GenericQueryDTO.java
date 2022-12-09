package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericQueryDTO {
    private Long id;
    private String title;
    private String genericQueryTypeId;
    private String technicalStaffId;
    private boolean publicQuery;
    private List<GenericQueryItemDTO> columns;
    private boolean totalizeFlag;
    private boolean groupFlag;
}
