package br.com.oi.sgis.entity;

import br.com.oi.sgis.util.BooleanToStringConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "ORCAMENTO_ITENS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemEstimate implements Serializable {

    private static final long serialVersionUID = 1415984605784231917L;
    @EmbeddedId
    private ItemEstimateID id;

    @Column(name = "OI_VALOR")
    private BigDecimal value;

    @Column(name = "OI_DATA_PREV")
    private LocalDateTime previsionDate;

    @Column(name = "OI_DATA_RETORNO")
    private LocalDateTime returnDate;

    @Column(name = "OI_DOC_RETORNO")
    private String returnDoc;

    @Column(name = "OI_DATA_DOC_RET")
    private LocalDateTime returnDocDate;

    @Column(name = "OI_ORC_FORNEC")
    private String provider;

    @Column(name = "OI_DATA_APROV")
    private LocalDateTime approvalDate;

    @Column(name = "OI_SITUACAO")
    private String situation;

    @Column(name = "OI_DATA_CANCEL")
    private LocalDateTime cancelDate;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "OI_ANALISADO")
    private boolean analyzed;

    @Transient
    private BigDecimal percentage;

    public BigDecimal getPercentage() {
        BigDecimal valueForPercentage = this.value !=null ?this.value : BigDecimal.ZERO;
        BigDecimal areaValue = id.getTicketIntervention().getUnity().getUnityCode().getValue()!=null ?id.getTicketIntervention().getUnity().getUnityCode().getValue() : BigDecimal.ONE;
        return (valueForPercentage.divide(areaValue, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100));
    }
}
