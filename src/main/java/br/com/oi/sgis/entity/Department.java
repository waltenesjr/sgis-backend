package br.com.oi.sgis.entity;

import br.com.oi.sgis.util.BooleanToStringConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "DEPTOS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Department implements Serializable {

    private static final long serialVersionUID = -6141585751628427297L;

    @Id
    @Column(name = "D_SIGLA")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "D_GERENTE")
    private TechnicalStaff manager;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "D_COD_ENDERECO")
    private Address address;

    @Column(name = "D_DIRETORIA")
    private String directorship;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "D_CONTATO")
    private TechnicalStaff contact;

    @Column(name = "D_DESCRICAO")
    private String description;

    @Column(name = "D_TELEFONE")
    private String phone;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "D_CENTRO_REPARO")
    private boolean repairCenter;

    @Column(name = "D_CENTRO_DEFAULT")
    private String repairCenterDefault;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "D_CENTRO_SOBRESS")
    private boolean spareCenter;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "D_FLAG_SIST")
    private boolean systemFlag;

    @Column(name = "D_CENTRO_RESP")
    private String responsibleCenter;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "D_INATIVO")
    private boolean inactive;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "D_OBRIGA_ENCAMINHAMENTO")
    private boolean obligateFowarding;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "D_BLOQ_NAO_DESIG")
    private boolean notDesignatedBloq;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "D_BLOQ_NAO_TRIADO")
    private boolean unscreenedBlock;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "D_BLOQ_NAO_ANALISADO")
    private boolean unanalyzedLock;

    @Column(name = "D_CENTRO_CUSTO")
    private String costCenter;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "D_ANALISE_ORC_CONTRATO")
    private boolean contractBudgetAnalysis;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "D_OBRIGA_USO")
    private boolean obligateUse;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "D_PEND_DEF")
    private boolean pendDef;
}
