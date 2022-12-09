package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "EQUIPAMENTO_AREA")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AreaEquipament implements Serializable {

    private static final long serialVersionUID = -2903208911730990535L;

    @Id
    @Column(name = "COD_AREA_FABRIC")
    private String id;

    @ManyToOne
    @JoinColumn(name = "COD_MODELO_EQTO", nullable = false)
    private ModelEquipamentType equipModelCode;

    @Column(name = "DESCRICAO", nullable = false)
    private String description;

    @Column(name = "VALOR_AREA", nullable = false)
    private BigDecimal value;

    @Column(name = "PESO", nullable = false)
    private BigDecimal weight;

    @Column(name = "QTD_AREA_MODELO", nullable = false)
    private Integer plaqueQuantity;

    @Column(name = "COD_BARRAS", nullable = false)
    private String barcode;

    @Column(name = "DIAS_CALIBR")
    private Integer adjustDays;

    @ManyToOne
    @JoinColumn(name = "TECNICO")
    private TechnicalStaff technician;

    @Column(name = "DESCR_ABREV")
    private String abrevDescription;

    @Column(name = "DATA")
    private LocalDateTime date;

    @Column(name = "MNEMONICO")
    private String mnemonic;

    @ManyToOne
    @JoinColumn(name = "E_CGC_CPF")
    private Company company;

    @Column(name = "CLASS_FISCAL")
    private String fiscalClassification;

    @Column(name = "COD_TECNICA")
    private String techniqueCode;

    @Column(name = "FLAG_DESCONT")
    private Boolean discontinuedFlag;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.equipament")
    private List<ElectricalPropEquip> electricalProperties;

}
