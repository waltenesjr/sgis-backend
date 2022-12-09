package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "PROP_ELETRICAS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElectricalProperty implements Serializable {

    private static final long serialVersionUID = -7147530585526265887L;
    @Id
    @Column(name = "PE_COD_PROP", nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "PE_COD_UN_MED", nullable = false)
    private Measurement measurement;

    @Column(name = "PE_DESCRICAO")
    private String description;
}
