package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "CONSULTAS_TIPOS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericQueryType implements Serializable {

    private static final long serialVersionUID = 850058390067934100L;

    @Id
    @Column(name = "CO_COD_TIPO")
    private String id;

    @Column(name = "CO_DESCRICAO")
    private String description;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "domainID.id")
    private List<Domain> columns;
}
