package br.com.oi.sgis.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "PARAMETROS")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Parameter implements Serializable {

    private static final long serialVersionUID = 907298385202445295L;
    @Id
    @Column(name = "P_CGC_CPF_EMPR")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "P_CGC_CPF_EMPR", insertable = false, updatable = false)
    private Company company;

    @Column(name = "P_VERSAO", nullable = false)
    private BigDecimal version;

    @Column(name = "P_MENSAGEM")
    private String message;

    @Column(name = "P_COD_CAIXA")
    private String boxCode;

    @Column(name = "P_COD_BARRAS")
    private String barcode;

    @Column(name = "P_PREFIXO", nullable = false)
    private String prefix;

    @Column(name = "P_HOMEM_HORA")
    private BigDecimal manHour;

    @Column(name = "P_SIGLA", nullable = false)
    private String abbreviation;

    @Column(name = "P_TIMEOUT")
    private BigDecimal timeout;

    @Column(name = "P_FTP")
    private String ftp;

}
