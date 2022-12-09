package br.com.oi.sgis.entity;

import br.com.oi.sgis.enums.MovTypeEnum;
import br.com.oi.sgis.enums.SignalMovTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "TIPOS_MOV_EST")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComponentMovType implements Serializable {

    private static final long serialVersionUID = 9157579420983460312L;
    @Id
    @Column(name = "TM_COD_TIPO_MOV")
    private String id;

    @Column(name = "TM_DESCRICAO", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "TM_TIPO_MOV", nullable = false)
    private MovTypeEnum type;

    @Enumerated(EnumType.STRING)
    @Column(name = "TM_SINAL", nullable = false)
    private SignalMovTypeEnum signal;
}
