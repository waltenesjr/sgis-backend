package br.com.oi.sgis.entity;

import br.com.oi.sgis.util.BooleanToStringConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "SITUACAO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Situation implements Serializable {

    private static final long serialVersionUID = 8883493033652062249L;
    @Id
    @Column(name = "S_COD_SIT")
    private String id;

    @Column(name = "S_DESCRICAO", nullable = false)
    private String description;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "S_CONCILIA")
    private Boolean conciliate;


}
