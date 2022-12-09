package br.com.oi.sgis.entity;

import br.com.oi.sgis.util.BooleanToStringConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "CENTRAIS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Central implements Serializable {

    private static final long serialVersionUID = 8412242558382012390L;
    @Id
    @Column(name = "CN_COD_CENTRAL")
    private String id;

    @ManyToOne
    @JoinColumn(name = "CN_COD_ESTACAO")
    private Station station;

    @Column(name = "CN_DESCRICAO", nullable = false)
    private String description;

    @Column(name = "CN_PREFIXO")
    private String prefix;

    @Column(name = "CN_TOMBAMENTO")
    private String tipping;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "CN_FLAG_ATIVO")
    private boolean activeFlag;
}
