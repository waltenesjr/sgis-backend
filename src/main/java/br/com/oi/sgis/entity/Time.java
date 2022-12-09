package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "TEMPOS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Time implements Serializable {
    private static final long serialVersionUID = -419741965607857690L;
    @EmbeddedId
    private TimeID id;

    @Column(name = "TP_TEMPO")
    private BigDecimal centesimalTime;

    @Column(name = "TP_PROCEDIMENTO")
    private String procedure;
}
