package br.com.oi.sgis.entity;

import br.com.oi.sgis.util.BooleanToStringConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "CONTRATOS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contract implements Serializable {

    private static final long serialVersionUID = -2565529777956217396L;
    @Id
    @Column(name = "CT_COD_CONTRATO")
    private String id;

    @ManyToOne
    @JoinColumn(name = "CT_CGC_CPF", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "CT_SIGLA", nullable = false)
    private Department department;

    @Column(name = "CT_DESCRICAO")
    private String extensiveDescription;

    @Column(name = "CT_PRIORIDADE")
    private Integer priority;

    @Column(name = "CT_VALOR")
    private BigDecimal value;

    @Column(name = "CT_QUANT")
    private Integer amount;

    @Column(name = "CT_VALOR_REALIZADO")
    private BigDecimal realizedValue;

    @Column(name = "CT_QUANT_REALIZADA")
    private Integer realizedAmount;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "CT_FLAG_COMPRA")
    private boolean acquisition;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "CT_FLAG_MANUTENCAO")
    private boolean maintenance;

    @Column(name = "CT_DATA_INICIO")
    private LocalDateTime initialDate;

    @Column(name = "CT_DATA_FIM")
    private LocalDateTime finalDate;

    @Column(name = "CT_DIAS_GARANTIA")
    private Integer warrantyDays;

    @Column(name = "CT_DIAS_ENTREGA")
    private Integer deliveryDays;

    @Column(name = "CT_DESCR_ABREV")
    private String description;

    @Column(name = "CT_DATA_CANCELA")
    private LocalDateTime cancelDate;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.contract")
    private List<ModelContract> modelContracts;
}
