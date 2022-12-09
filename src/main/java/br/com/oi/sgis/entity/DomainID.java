package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DomainID implements Serializable {
    private static final long serialVersionUID = -4179776044648695447L;
    @Column(name = "COD_DO", nullable = false)
    private String id;

    @Column(name = "NUM_SEQ_DO", nullable = false)
    private Integer sequenceNumber;
}
