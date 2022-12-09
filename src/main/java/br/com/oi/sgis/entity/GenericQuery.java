package br.com.oi.sgis.entity;

import br.com.oi.sgis.util.BooleanToStringConverter;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table(name = "CONSULTAS")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericQuery implements Serializable {

    private static final long serialVersionUID = 850058390067934100L;

    @Id
    @Column(name = "CN_COD_CONSULTA", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CN_COD_TIPO")
    private GenericQueryType genericQueryType;

    @ManyToOne
    @JoinColumn(name = "CN_TECNICO")
    private TechnicalStaff technicalStaff;

    @Column(name = "CN_TITLE")
    private String title;

    @Column(name = "CN_PUBLIC")
    @Convert(converter = BooleanToStringConverter.class)
    private boolean publicQuery;

    @Column(name = "CN_FLAG_TOT")
    @Convert(converter= BooleanToStringConverter.class)
    private boolean totalizeFlag;

    @Column(name = "CN_FLAG_GROUP")
    @Convert(converter= BooleanToStringConverter.class)
    private boolean groupFlag;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.genericQuery")
    private List<GenericQueryItem> genericQueryItems;
}


