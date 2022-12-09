package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketInterventionID implements Serializable {
    private static final long serialVersionUID = 9065728548698439729L;
    @ManyToOne
    @JoinColumn(name = "IB_NUM_BR", nullable = false)
    private RepairTicket repairTicket;

    @Column(name = "IB_SEQ", nullable = false)
    private Long sequence;
}
