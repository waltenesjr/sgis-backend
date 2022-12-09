package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "TBREC_INFORMATICA")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InformaticsRec implements Serializable {

    private static final long serialVersionUID = 8295314981319677006L;
    @Id
    @Column(name = "ID_UNICO_INTERFACE", nullable = false)
    private Long id;

    @Column(name = "OPERACAO", nullable = false, length = 4)
    private String operation;

    @ManyToOne
    @JoinColumn(name = "ID_EQUIPAMENTO", nullable = false)
    private Unity unity;

    @ManyToOne
    @JoinColumn(name = "ID_EQUIPAMENTO_INVENTARIO")
    private Unity inventoryUnity;

    @Column(name = "ID_SAP_PM", length = 18)
    private String sapId;

    @Column(name = "DATA_PROCESSAMENTO")
    private LocalDateTime processingDate;

    @Column(name = "COD_MENSAGEM", length = 4)
    private String messageCode;

    @Column(name = "MENSAGEM", length = 80)
    private String message;

    @Column(name = "PART_NUMBER", length = 50)
    private String partNumber;

    @Column(name = "DATA_LEITURA_INF", nullable = false)
    private LocalDateTime infoReadingDate;

    @Column(name = "PODE_REPROCESSAR")
    private Integer canBeReprocessed;

    @Column(name = "EMPRESA_CONTABIL")
    private String accountantCompany;
}
