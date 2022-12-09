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
public class ModelTechnicianID implements Serializable {

    private static final long serialVersionUID = -794678757730154343L;
    @ManyToOne
    @JoinColumn(name = "TM_SIGLA", nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "TM_COD_TECNICO", nullable = false)
    private TechnicalStaff technicalStaff;

    @ManyToOne
    @JoinColumn(name = "TM_COD_PLACA", nullable = false)
    private AreaEquipament model;
}
