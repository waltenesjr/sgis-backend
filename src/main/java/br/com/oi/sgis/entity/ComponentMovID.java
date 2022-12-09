package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComponentMovID implements Serializable {
    private static final long serialVersionUID = -6369580084458147412L;

    @JoinColumn(name = "CM_SIGLA", referencedColumnName = "CD_SIGLA", nullable = false)
    @JoinColumn(name = "CM_COD_COMP", referencedColumnName = "CD_COD_COMP", nullable = false)
    @ManyToOne
    private DepartmentComponent departmentComponent;

    @Column(name = "CM_SEQ", nullable = false)
    private Long sequence;
}
