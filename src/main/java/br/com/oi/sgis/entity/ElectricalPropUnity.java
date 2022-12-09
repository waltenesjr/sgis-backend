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
@Table(name = "PROP_ELETR_UN")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElectricalPropUnity implements Serializable {

    private static final long serialVersionUID = 3267580331191791757L;
    @EmbeddedId
    private ElectricalPropUnityID id;

    @Column(name = "PU_MEDIDA")
    private BigDecimal measure;

    @Column(name = "PU_VALOR")
    private BigDecimal value;
}
