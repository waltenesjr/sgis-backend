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
import java.util.stream.Collectors;

@Entity
@Table(name = "INTERVBILH")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketIntervention implements Serializable {

    private static final long serialVersionUID = 5344533898066819116L;
    @EmbeddedId
    private TicketInterventionID id;

    @ManyToOne
    @JoinColumn(name = "IB_COD_INTERV", nullable = false)
    private Intervention intervention;

    @ManyToOne
    @JoinColumn(name = "IB_OPERADOR", nullable = false)
    private TechnicalStaff operator;

    @ManyToOne
    @JoinColumn(name = "IB_TECNICO", nullable = false)
    private TechnicalStaff technician;

    @Column(name = "IB_DATA_INI")
    private LocalDateTime initialDate;

    @Column(name = "IB_DATA_FIM")
    private LocalDateTime finalDate;

    @Column(name = "IB_OBS", nullable = false)
    private String observation;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "IB_EXTERNO", nullable = false)
    private boolean externalRepair;

    @ManyToOne
    @JoinColumn(name = "IB_COD_BARRAS")
    private Unity unity;

    @Column(name = "IB_VALOR_MO")
    private BigDecimal laborValue;

    @Column(name = "IB_VALOR_CP")
    private BigDecimal cpValue;

    @ManyToOne
    @JoinColumn(name = "IB_COD_SIT_REP")
    private RepSituation repSituation;

    @Transient
    private ItemEstimate itemEstimate;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.ticketIntervention")
    private List<TicketDiagnosis> ticketDiagnosis;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ticketIntervention")
    private List<ComponentMov> ticketComponents;

    public List<ComponentMov> getTicketComponents() {
        if(ticketComponents!=null && !ticketComponents.isEmpty())
            return ticketComponents.stream().filter(c -> c.getQuantity() > 0).collect(Collectors.toList());
        return List.of();
    }

    public List<TicketDiagnosis> getTicketDiagnosis() {
        if(ticketDiagnosis == null)
            return List.of();
        return ticketDiagnosis;
    }
}
