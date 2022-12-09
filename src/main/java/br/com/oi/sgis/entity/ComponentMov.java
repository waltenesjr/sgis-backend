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
@Table(name = "COMPONENTES_MOV")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComponentMov implements Serializable {

    private static final long serialVersionUID = 578484585196591628L;
    @EmbeddedId
    private ComponentMovID id;

    @ManyToOne
    @JoinColumn(name = "CM_TECNICO", nullable = false)
    private TechnicalStaff technician;

    @ManyToOne
    @JoinColumn(name = "CM_COD_TIPO_MOV", nullable = false)
    private ComponentMovType componentMovType;

    @Column(name = "CM_DATA")
    private LocalDateTime date;

    @Column(name = "CM_QUANT")
    private Long quantity;

    @Column(name = "CM_SALDO_ANT")
    private Long previousBalance;

    @Column(name = "CM_DESCRICAO")
    private String description;

    @Column(name = "CM_DOCUMENTO")
    private String document;

    @JoinColumn(name = "CM_NUM_BR", referencedColumnName = "IB_NUM_BR")
    @JoinColumn(name = "CM_SEQ_IB", referencedColumnName = "IB_SEQ")
    @ManyToOne
    private TicketIntervention ticketIntervention;

    @Column(name = "CM_VALOR")
    private BigDecimal value;

    @Column(name = "CM_CUSTO_MOV")
    private BigDecimal movCost;

    @Column(name = "CM_CUSTO_ANT")
    private BigDecimal previousCost;
}
