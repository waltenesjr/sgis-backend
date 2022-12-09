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
public class ModelContractID implements Serializable {

    private static final long serialVersionUID = 3278489289779501081L;
    @ManyToOne
    @JoinColumn(name = "CS_COD_CONTRATO", nullable = false)
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "CS_COD_PLACA", nullable = false)
    private AreaEquipament model;
}
