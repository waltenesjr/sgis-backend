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
@Table(name = "PROP_ELETR_EA")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElectricalPropEquip implements Serializable {

    private static final long serialVersionUID = 3267580331191791757L;
    @EmbeddedId
    private ElectricalPropEquipID id;

    @Column(name = "PA_MEDIDA")
    private BigDecimal measure;

}
