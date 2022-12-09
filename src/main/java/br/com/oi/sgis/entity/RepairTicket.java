package br.com.oi.sgis.entity;

import br.com.oi.sgis.enums.PriorityRepairEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "BILHETE")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepairTicket implements Serializable {
    private static final long serialVersionUID = -5856319178587639628L;
    @Id
    @Column(name = "B_NUM_BR", nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "B_COD_ESTACAO")
    private Station station;

    @ManyToOne
    @JoinColumn(name = "B_COD_SIT_REP", nullable = false)
    private RepSituation situation;

    @ManyToOne
    @JoinColumn(name = "B_COD_CENTRAL")
    private Central grouping;

    @ManyToOne
    @JoinColumn(name = "B_COD_DEFEITO", nullable = false)
    private Defect defect;

    @ManyToOne
    @JoinColumn(name = "B_COD_BARRAS", nullable = false)
    private Unity unity;

    @ManyToOne
    @JoinColumn(name = "B_RESPONSAVEL", nullable = false)
    private TechnicalStaff technician;

    @ManyToOne
    @JoinColumn(name = "B_DEP_ORIGEM", nullable = false)
    private Department originDepartment;

    @Column(name = "B_QUEIXA")
    private String description;

    @Column(name = "B_DATA_ACEIT")
    private LocalDateTime acceptDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "B_PRIORI", nullable = false)
    private PriorityRepairEnum priority;

    @Column(name = "B_DOC_ANEXO")
    private String docAttached;

    @Column(name = "B_DATA_ENCERRA")
    private LocalDateTime closureDate;

    @ManyToOne
    @JoinColumn(name = "B_TECNICO", nullable = false)
    private TechnicalStaff operator;

    @Column(name = "B_DATA_DEV")
    private LocalDateTime devolutionDate;

    @Column(name = "B_OBS")
    private String observation;

    @Column(name = "B_DATA", nullable = false)
    private LocalDateTime openDate;

    @Column(name = "B_FLAG_IMPR")
    private Integer impressFlag;

    @Column(name = "B_DATA_CANCEL")
    private LocalDateTime cancelDate;

    @Column(name = "B_DATA_ACEIT_DEV")
    private LocalDateTime acceptDevDate;

    @ManyToOne
    @JoinColumn(name = "B_SIGLA_CR", nullable = false)
    private Department repairCenterDepartment;

    @Column(name = "B_VALOR_REPARO")
    private BigDecimal repairValue;

    @ManyToOne
    @JoinColumn(name = "B_TEC_DEFAULT")
    private TechnicalStaff repairTechnician;

    @Column(name = "B_SUBSTITUICAO")
    private String substitution;

    @Column(name = "B_DATA_NORMALIZ")
    private LocalDateTime normalizationDate;

    @ManyToOne
    @JoinColumn(name = "B_MANTENEDOR_DEFAULT")
    private Company maintainer;

    @ManyToOne
    @JoinColumn(name = "B_CONTRATO_DEFAULT")
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "B_DEPTO_DEVOL", nullable = false)
    private Department devolutionDepartment;

    @Column(name = "B_NF_DEVOL")
    private String devolutionNF;

    @Column(name = "B_NF_DATA")
    private LocalDateTime dateNF;

    @Column(name = "B_OBS_DEVOL")
    private String devolutionObs;

    @Column(name = "B_PREVISAO")
    private LocalDateTime prevision;

    @Column(name = "B_NUM_FAS")
    private String fasNumber;

    @Column(name = "B_NUMERO_BA")
    private String baNumber;

    @Column(name = "B_DATA_ENT_CSP_CS")
    private LocalDateTime cspCsDeliverDate;
}
