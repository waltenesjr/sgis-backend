package br.com.oi.sgis.entity;

import br.com.oi.sgis.util.BooleanToStringConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "DEPTOS_UNIDADES")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentUnity implements Serializable {
    private static final long serialVersionUID = -1999245085838069162L;

    @EmbeddedId
    private DepartmentUnityID id;

    @Column(name = "DU_ESTOQUE_MINIMO")
    private Long minStock;

    @Column(name = "DU_ESTOQUE_REPOS")
    private Long repositionStock;

    @Column(name = "DU_ESTOQUE_MAXIMO")
    private Long maxStock;

    @ManyToOne
    @JoinColumn(name = "DU_ESTACAO", nullable = false)
    private Station station;

    @Column(name = "DU_LOCALIZACAO")
    private String location;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "DU_INATIVA")
    private boolean inactive;
}
