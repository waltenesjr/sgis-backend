package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemEstimateID implements Serializable {
    private static final long serialVersionUID = -6120630066553560698L;

    @JoinColumn(name="OI_NUM_BR", referencedColumnName="IB_NUM_BR", nullable = false)
    @JoinColumn(name="OI_SEQ", referencedColumnName="IB_SEQ", nullable = false)
    @ManyToOne
    private TicketIntervention ticketIntervention;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OI_NUM_ORC", nullable = false)
    private Estimate estimate;
}
