package br.com.oi.sgis.entity;

import br.com.oi.sgis.enums.CurrencyTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "DOCUMENTOS_FISCAIS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FiscalDocument implements Serializable {

    private static final long serialVersionUID = 8242055026703221636L;
    @EmbeddedId
    private FiscalDocumentId id;

    @Column(name = "DF_DATA_ATIVACAO")
    private LocalDateTime activationDate;

    @Column(name = "DF_VALOR")
    private BigDecimal value;

    @ManyToOne
    @JoinColumn(name = "DF_COD_CONTRATO")
    private Contract contract;

    @Column(name = "DF_USUARIO")
    private String user;

    @Column(name = "DF_DATA")
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name = "DF_TP_MOEDA")
    private CurrencyTypeEnum currencyType;

    @Column(name = "DF_EMP_CONTABIL")
    private String accountingCompany;

    public String idNumDoc(){
        return id.getDocNumber().toString();
    }

}
