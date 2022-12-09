package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketDiagnosisID implements Serializable {

    private static final long serialVersionUID = 850058390067934100L;

    @JoinColumn(name = "DB_NUM_BR", referencedColumnName = "IB_NUM_BR", nullable = false)
    @JoinColumn(name = "DB_SEQ", referencedColumnName = "IB_SEQ", nullable = false)
    @ManyToOne
    private TicketIntervention ticketIntervention;

    @ManyToOne
    @JoinColumn(name = "DB_COD_DIAG", nullable = false)
    private Diagnosis diagnosis;
}
