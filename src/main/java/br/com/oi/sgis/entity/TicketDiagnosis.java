package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "DIAG_BILHETE")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketDiagnosis implements Serializable {

    private static final long serialVersionUID = -8005104810196813164L;
    @EmbeddedId
    private TicketDiagnosisID id;

    @Column(name = "DB_POS_ESQUEMA", nullable = false)
    private String position;

}
