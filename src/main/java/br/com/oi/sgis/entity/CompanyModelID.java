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
public class CompanyModelID implements Serializable {
    private static final long serialVersionUID = -7051020711367737216L;
    @ManyToOne
    @JoinColumn(name = "EM_CGC_CPF", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "EM_COD_PLACA", nullable = false)
    private AreaEquipament equipament;

    @ManyToOne
    @JoinColumn(name = "EM_SIGLA", nullable = false)
    private Department department;
}
