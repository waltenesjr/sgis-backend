package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "TIPO_MODELO_EQPTO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelEquipamentType implements Serializable {

    private static final long serialVersionUID = -8321783129636106550L;
    @Id
    @Column(name = "COD_MODELO_EQTO")
    private String id;

    @ManyToOne
    @JoinColumn(name = "E_CGC_CPF")
    private Company company;

    @Column(name = "DESCR_TIPO_MODELO")
    private String description;

    @ManyToOne
    @JoinColumn(name = "COD_TIPO_EQTO")
    private EquipamentType equipamentType;

    @ManyToOne
    @JoinColumn(name = "TECNICO")
    private TechnicalStaff technician;

    @Column(name = "DATA")
    private LocalDateTime date;

    @Column(name = "MNEMONICO")
    private String mnemonic;

    @ManyToOne
    @JoinColumn(name = "COD_TECNOLOGIA")
    private Technology technology;

    @Column(name = "CENTRO_CUSTO")
    private String costCenter;

    @Column(name = "CONTA_SAP")
    private String accountSap;

    @Column(name = "COD_TECNICA")
    private String techniqueCode;

    @Column(name = "FLAG_DESCONT")
    private Boolean descountFlag;


}
