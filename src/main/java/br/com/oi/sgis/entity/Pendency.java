package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "PENDENCIAS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pendency implements Serializable {

    private static final long serialVersionUID = 382290821704615323L;
    @Id
    @Column(name = "PN_COD_PLACA")
    private String id;

    @MapsId
    @ManyToOne
    @JoinColumn(name = "PN_COD_PLACA")
    private AreaEquipament equipament;


    @ManyToOne
    @JoinColumn(name = "PN_COD_BARRAS", nullable = false)
    private Unity unity;

    @Column(name = "PN_MOV_ORIGEM", nullable = false)
    private String originMov;

    @ManyToOne
    @JoinColumn(name = "PN_SIGLA_LOCAL")
    private Department departmentOrigin;

    @ManyToOne
    @JoinColumn(name = "PN_PARA_LOCAL")
    private Department departmentDestination;

    @ManyToOne
    @JoinColumn(name = "PN_TECNICO")
    private TechnicalStaff technician;

    @ManyToOne
    @JoinColumn(name = "PN_OPERADOR")
    private TechnicalStaff operator;

    @ManyToOne
    @JoinColumn(name = "PN_CGC_CPF_RESP")
    private Company serviceProvider;

    @Column(name = "PN_PRAZO")
    private LocalDateTime deadline;

    @Column(name = "PN_DATA")
    private LocalDateTime initialDate;

    @Column(name = "PN_BAIXA")
    private LocalDateTime finalDate;

    @Column(name = "PN_OBS")
    private String observation;

    @Column(name = "PN_FLAG")
    private String flag;

}
