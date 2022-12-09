package br.com.oi.sgis.entity;

import br.com.oi.sgis.util.BooleanToStringConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import java.io.Serializable;

@Entity
@Table(name = "CAIXAS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Box implements Serializable {

    private static final long serialVersionUID = 7186350395081462151L;
    @Id
    @Positive
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CX_COD_CAIXA")
    private String id;

    @Column(name = "CX_SIGLA", nullable = false)
    private String abbreviation ;

    @Column(name = "CX_DESCRICAO")
    private String description;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "CX_INATIVO")
    private boolean active;

    @Column(name = "CX_LOCACAO")
    private String location;

    @ManyToOne
    @JoinColumn(name = "CX_COD_ESTACAO")
    private Station station;

    @ManyToOne
    @JoinColumn(name = "CX_COD_TIPO_CAIXA")
    private BoxType boxType;


}
