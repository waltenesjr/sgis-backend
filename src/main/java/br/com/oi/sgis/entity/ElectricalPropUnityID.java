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
public class ElectricalPropUnityID implements Serializable {

    private static final long serialVersionUID = -4907904819786822551L;
    @ManyToOne
    @JoinColumn(name = "PU_COD_PROP", nullable = false)
    private ElectricalProperty properties;

    @ManyToOne
    @JoinColumn(name = "PU_COD_BARRAS", nullable = false)
    private Unity unity;
}
