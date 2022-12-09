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
public class TimeID implements Serializable {
    private static final long serialVersionUID = -1074274851670275862L;
    @ManyToOne
    @JoinColumn(name = "TP_COD_INTERV", nullable = false)
    private Intervention intervention;

    @ManyToOne
    @JoinColumn(name = "TP_COD_PLACA", nullable = false)
    private AreaEquipament unityModel;
}
