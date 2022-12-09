package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "PEDIDOS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    private static final long serialVersionUID = -647941012299270508L;
    @Id
    @Column(name = "PD_COD_PEDIDO")
    private String id;

    @ManyToOne
    @JoinColumn(name = "PD_SIGLA", nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "PD_TECNICO", nullable = false)
    private TechnicalStaff technicalStaff;

    @Column(name = "PD_DATA", nullable = false)
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "PD_COD_PLACA", nullable = false)
    private AreaEquipament areaEquipament;

    @Column(name = "PD_URGENCIA")
    private String urgency;

    @Column(name = "PD_OBS")
    private String observation;

    @Column(name = "PD_SITUACAO")
    private String situacao;

    @ManyToOne
    @JoinColumn(name = "PD_SIGLA_PREF")
    private Department prefAbbreviation;

    @Column(name = "PD_QTDE_PED")
    private int demandedQuantity;

    @Column(name = "PD_QTDE_ATEND")
    private int servedQuantiy;
}
