package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentComponentID implements Serializable {
    private static final long serialVersionUID = -8498613427275829154L;

    @ManyToOne
    @JoinColumn(name = "CD_COD_COMP", nullable = false)
    private Component component;

    @ManyToOne
    @JoinColumn(name = "CD_SIGLA", nullable = false)
    private Department department;
}
