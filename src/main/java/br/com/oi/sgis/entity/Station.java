package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ESTACOES")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Station implements Serializable {

    private static final long serialVersionUID = 5688422238095448153L;
    @Id
    @Column(name = "ES_COD_ESTACAO")
	private String id;

    @Column(name = "ES_DESCRICAO", nullable = false)
    private String description;

    @Column(name = "ES_NUM_ESTACAO", nullable = false)
    private BigDecimal number;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name= "ES_COD_ENDERECO")
    private Address address;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ES_UF")
    private Uf uf;

}
