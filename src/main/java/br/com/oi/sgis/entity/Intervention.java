package br.com.oi.sgis.entity;

import br.com.oi.sgis.util.BooleanToStringConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "INTERVENCAO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Intervention implements Serializable {

    private static final long serialVersionUID = -6723351145239798020L;
    @Id
    @Column(name = "IN_COD_INTERV", nullable = false)
    private String id;

    @Column(name = "IN_DESCRICAO", nullable = false)
    private String description;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "IN_FLAG_INTERNO", nullable = false)
    private Boolean internalFlag;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "IN_FLAG_EXTERNO", nullable = false)
    private Boolean externalFlag;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "IN_FLAG_CALIBR", nullable = false)
    private Boolean calibrationFlag;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "IN_FLAG_PROD", nullable = false)
    private Boolean productiveFlag;
}
