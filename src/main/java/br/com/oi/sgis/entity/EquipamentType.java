package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "SGE_TIPO_EQPTO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipamentType implements Serializable {

    private static final long serialVersionUID = -9024752852635107868L;
    @Id
    @Column(name = "COD_TIPO_EQTO", nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "AP_COD_APLICACAO", nullable = false)
    private Application application;

    @Column(name = "NOME_TIPO_EQTO", nullable = false)
    private String equipamentName;

    @ManyToOne
    @JoinColumn(name = "COD_TECNICA", nullable = false)
    private Technique technique;
}
