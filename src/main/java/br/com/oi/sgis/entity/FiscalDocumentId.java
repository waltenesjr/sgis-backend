package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FiscalDocumentId implements Serializable {

    @Column(name = "DF_NUM_DOC", nullable = false)
    private Long docNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DF_CGC_CPF", nullable = false)
    private Company cgcCPf;

    @Column(name="DF_DATA_DOC", nullable = false)
    private LocalDateTime docDate;
}
