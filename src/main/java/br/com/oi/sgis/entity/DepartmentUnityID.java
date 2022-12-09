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
public class DepartmentUnityID implements Serializable {

    private static final long serialVersionUID = -1680052095365933626L;
    @ManyToOne
    @JoinColumn(name = "DU_COD_PLACA", nullable = false)
    private AreaEquipament equipament;

    @ManyToOne
    @JoinColumn(name = "DU_SIGLA", nullable = false)
    private Department department;
}
