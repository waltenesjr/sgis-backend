package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElectricalPropEquipID implements Serializable {

    @ManyToOne
    @JoinColumn(name = "PA_COD_PROP", nullable = false)
    private ElectricalProperty properties;

    @ManyToOne
    @JoinColumn(name = "PA_COD_PLACA", nullable = false)
    private AreaEquipament equipament;
}
