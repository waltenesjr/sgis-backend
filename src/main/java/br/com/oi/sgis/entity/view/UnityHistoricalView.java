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
@Table(name = "V_UN_HISTORICO")
public class UnityHistoricalView {

    @Id
    private Long id;
    @Column(name = "LOG_COD_BARRAS")
    private String barcode;
    @Column(name = "LOG_DATA")
    private LocalDateTime date;
    @Column(name = "DE_SERIE")
    private String fromSeriesNumber;
    @Column(name = "PARA_SERIE")
    private String toSeriesNumber;
    @Column(name = "DE_COD_SIT")
    private String fromSituationCode;
    @Column(name = "PARA_COD_SIT")
    private String toSituationCode;
    @Column(name = "DE_SIGLA_PROP")
    private String fromResponsible;
    @Column(name = "PARA_SIGLA_PROP")
    private String toResponsible;
    @Column(name = "DE_COD_CENTRAL")
    private String fromCentralCode;
    @Column(name = "PARA_COD_CENTRAL")
    private String toCentralCode;
    @Column(name = "DE_COD_ESTACAO")
    private String fromStationCode;
    @Column(name = "PARA_COD_ESTACAO")
    private String toStationCode;
    @Column(name = "DE_COD_ENDERECO")
    private String fromAddressCode;
    @Column(name = "PARA_COD_ENDERECO")
    private String toAddressCode;
    @Column(name = "DE_COD_CAIXA")
    private String fromBoxCode;
    @Column(name = "PARA_COD_CAIXA")
    private String toBoxCode;
    @Column(name = "DE_COD_PLACA")
    private String fromUnityCode;
    @Column(name = "PARA_COD_PLACA")
    private String toUnityCode;
    @Column(name = "DE_CGC_CPF_RESP")
    private String fromProviderResponsible;
    @Column(name = "PARA_CGC_CPF_RESP")
    private String toProviderResponsible;
    @Column(name = "DE_SIGLA_LOCAL")
    private String fromDeposit;
    @Column(name = "PARA_SIGLA_LOCAL")
    private String toDeposit;
    @Column(name = "DE_TECNICO")
    private String fromTechnician;
    @Column(name = "PARA_TECNICO")
    private String toTechnician;
    @Column(name = "DE_PRAZO")
    private String fromDeadline;
    @Column(name = "PARA_PRAZO")
    private String toDeadline;
    @Column(name = "DE_PARA_LOCAL")
    private String fromDestination;
    @Column(name = "PARA_PARA_LOCAL")
    private String toDestination;
    @Column(name = "DE_GARANTIA")
    private String fromWarranty;
    @Column(name = "PARA_GARANTIA")
    private String toWarranty;
    @Column(name = "DE_VALOR")
    private String fromValue;
    @Column(name = "PARA_VALOR")
    private String toValue;
    @Column(name = "DE_NUM_TP")
    private String fromTpNumber;
    @Column(name = "PARA_NUM_TP")
    private String toTpNumber;
    @Column(name = "DE_BASTIDOR")
    private String fromInstallationRack;
    @Column(name = "PARA_BASTIDOR")
    private String toInstallationRack;
    @Column(name = "DE_CGC_CPF_INST")
    private String fromClient;
    @Column(name = "PARA_CGC_CPF_INST")
    private String toClient;
    @Column(name = "DE_DOC_TRANSF")
    private String fromTransferDoc;
    @Column(name = "PARA_DOC_TRANSF")
    private String toTransferDoc;
    @Column(name = "DE_COD_BARRAS_PAI")
    private String fromBarcodeParent;
    @Column(name = "PARA_COD_BARRAS_PAI")
    private String toBarcodeParent;
    @Column(name = "DE_DATA_CALIBR")
    private String fromAdjustDate;
    @Column(name = "PARA_DATA_CALIBR")
    private String toAdjustDate;
    @Column(name = "DE_LOCACAO")
    private String fromLocation;
    @Column(name = "PARA_LOCACAO")
    private String toLocation;
    @Column(name = "DE_DATA_SIT")
    private String fromSituationDateChange;
    @Column(name = "PARA_DATA_SIT")
    private String toSituationDateChange;
    @Column(name = "DE_TOMBAMENTO")
    private String fromTipping;
    @Column(name = "PARA_TOMBAMENTO")
    private String toTipping;
    @Column(name = "DE_OBSERV")
    private String fromObservation;
    @Column(name = "PARA_OBSERV")
    private String toObservation;
}
