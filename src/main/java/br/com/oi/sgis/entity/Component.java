package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "COMPONENTES")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Component implements Serializable {

    private static final long serialVersionUID = -8161841274319258666L;
    @Id
    @Column(name = "CP_COD_COMP")
    private String id;

    @ManyToOne
    @JoinColumn(name = "CP_COD_TIPO_COMP")
    private ComponentType componentType;

    @Column(name = "CP_DESCRICAO", nullable = false)
    private String description;

    @Column(name = "CP_VALOR", nullable = false)
    private BigDecimal value;
}
