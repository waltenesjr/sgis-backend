package br.com.oi.sgis.dto;

import br.com.oi.sgis.entity.view.MovItensView;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data @Builder
public class MovItensViewReportDTO {
    private List<MovItensView> groupItens;
    private int totalItens;
}
