package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MOV_DIVISIVEIS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CableMovement implements Serializable {
    private static final long serialVersionUID = 6828047273203970218L;
    @EmbeddedId
    private CableMovementID id;

    @Column(name = "MD_DATA")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "MD_COD_PROP", nullable = false)
    private ElectricalProperty electricalProperty;

    @ManyToOne
    @JoinColumn(name = "MD_COD_TIPO_MOV", nullable = false)
    private ComponentMovType componentMovType;

    @Column(name = "MD_QUANT")
    private Long quantity;

    @Column(name = "MD_VALOR")
    private BigDecimal value;

    @ManyToOne
    @JoinColumn(name = "MD_TECNICO", nullable = false)
    private TechnicalStaff technician;

    @Column(name = "MD_SALDO_ANT")
    private BigDecimal previousBalance;

    @Column(name = "MD_CUSTO_ANT")
    private BigDecimal previousCost;

    @Column(name = "MD_DOCUMENTO")
    private String document;

    @ManyToOne
    @JoinColumn(name = "MD_SIGLA", nullable = false)
    private Department department;

    @Column(name = "MD_OBRA")
    private String job;


}
