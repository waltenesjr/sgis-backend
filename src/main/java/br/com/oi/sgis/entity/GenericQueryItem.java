package br.com.oi.sgis.entity;

import br.com.oi.sgis.util.BooleanToStringConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "CONSULTA_ITENS")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericQueryItem implements Serializable {

    private static final long serialVersionUID = 5344533898066819116L;

    @EmbeddedId
    private GenericQueryItemID id;

    @Column(name = "CI_COD_COL")
    private String columnName;

    @Column(name = "CI_DESCR_WIDTH")
    private int widthDescription;

    @Column(name = "CI_COD_WIDTH")
    private int widthCode;

    @Column(name = "CI_FLAG_NONULLS")
    @Convert(converter= BooleanToStringConverter.class)
    private boolean flagNonull;

    @Column(name = "CI_FLAG_ORDER")
    @Convert(converter= BooleanToStringConverter.class)
    private boolean flagOrder;

    @Column(name = "CI_VAL1")
    private String valueOne;

    @Column(name = "CI_VAL2")
    private String valueTwo;

    @Column(name = "CI_FLAG_NULLS")
    @Convert(converter= BooleanToStringConverter.class)
    private boolean flagNulls;

    @Column(name = "CI_NOSHOW")
    @Convert(converter= BooleanToStringConverter.class)
    private boolean noShow;

    @Column(name = "CI_OPERATOR")
    private String operator;
}
