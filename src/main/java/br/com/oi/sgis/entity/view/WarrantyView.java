package br.com.oi.sgis.entity.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "V_GARANTIA ")
public class WarrantyView {
    @Id
    private Long id;
    @Column(name = "COD_BARRAS")
    private String unityId;
    @Column(name = "PRESTADOR")
    private String provider;
    @Column(name = "NUM_BR")
    private String brNumber;
    @Column(name = "CONTRATO")
    private String contract;
    @Column(name = "DOC_COMPRA")
    private String purchaseDoc;
    @Column(name = "DATA_GARANTIA")
    private LocalDateTime warrantyDate;
}
