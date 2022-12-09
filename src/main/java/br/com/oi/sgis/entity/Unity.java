package br.com.oi.sgis.entity;

import br.com.oi.sgis.enums.SituationEnum;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "UNIDADES")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Unity implements Serializable {

    private static final long serialVersionUID = 9005664877310583488L;
    @Id
    @Column(name = "UN_COD_BARRAS")
    private String id;

    @Column(name = "UN_COD_CONTRATO")
    private String contractCode;

    @ManyToOne
    @JoinColumn(name = "UN_COD_CENTRAL")
    private Central central;

    @ManyToOne
    @JoinColumn(name = "UN_COD_ESTACAO")
    private Station station;

    @ManyToOne
    @JoinColumn(name = "UN_COD_ENDERECO")
    private Address address;

    @ManyToOne
    @JoinColumn(name = "UN_COD_CAIXA")
    private Box box;

    @ManyToOne
    @JoinColumn(name = "UN_COD_PLACA",nullable = false)
    private AreaEquipament unityCode;

    @ManyToOne
    @JoinColumn(name = "UN_COD_SIT",nullable = false)
    private Situation situationCode;

    @ManyToOne
    @JoinColumn(name = "UN_TECNICO", nullable = false)
    private TechnicalStaff technician;

    @ManyToOne
    @JoinColumn(name = "UN_SIGLA_LOCAL", nullable = false)
    private Department deposit;

    @ManyToOne
    @JoinColumn(name = "UN_SIGLA_PROP", nullable = false)
    private Department responsible;

    @ManyToOne
    @JoinColumn(name = "UN_PARA_LOCAL")
    private Department destination;

    @Column(name = "UN_PRAZO")
    private LocalDateTime deadline;

    @Column(name = "UN_OBSERV")
    private String observation;

    @Column(name = "UN_GARANTIA")
    private LocalDateTime warrantyDate;

    @Column(name = "UN_VALOR")
    private BigDecimal value;

    @Column(name = "UN_LATITUDE")
    private String latitude;

    @Column(name = "UN_LONGITUDE")
    private String longitude;

    @Column(name = "UN_NUM_TP")
    private String tpNumber;

    @ManyToOne
    @JoinColumn(name = "UN_CGC_CPF_RESP")
    private Company providerResponsible;

    @ManyToOne
    @JoinColumn(name = "UN_CGC_CPF_INST")
    private Company client;

    @Column(name = "UN_SERIE")
    private String serieNumber;

    @Column(name = "UN_DOC_TRANSF")
    private String transferDoc;

    @Column(name = "UN_COD_BARRAS_PAI")
    private String barcodeParent;

    @Column(name = "UN_BASTIDOR")
    private String installationRack;

    @Column(name = "UN_DATA_CALIBR")
    private LocalDateTime adjustDate;

    @Column(name = "UN_LOCACAO")
    private String location;

    @Column(name = "UN_DATA_SIT")
    private LocalDateTime situationDateChange;

    @Column(name = "UN_TOMBAMENTO")
    private String tipping;

    @Column(name = "UN_VERSAO")
    private String version;

    @JoinColumn(name="UN_NF_CGC_CPF", referencedColumnName="DF_CGC_CPF")
    @JoinColumn(name="UN_NF_NUM_DOC", referencedColumnName="DF_NUM_DOC")
    @JoinColumn(name ="UN_NF_DATA_DOC", referencedColumnName = "DF_DATA_DOC")
    @ManyToOne
    private FiscalDocument fiscalDocument;

    @Column(name = "UN_DATA_CADASTRO")
    private LocalDateTime registerDate;

    @ManyToOne
    @JoinColumn(name = "UN_COD_ESTACAO_INST")
    private Station stationInst;

    @ManyToOne
    @JoinColumn(name = "UN_TECNICO_INSTALA")
    private TechnicalStaff installationTechnician;

    @Column(name = "UN_NUMERO_BA")
    private String baNumber;

    @Column(name = "UN_CONTADOR")
    private Integer provider;

    @Column(name = "UN_CONTADOR_PRESTADOR")
    private Integer providerAccountant;

    @Column(name = "UN_UF")
    private String uf;

    @Column(name = "UN_NUMERO_ITEM")
    private String orderNumber;

    @Column(name = "UN_PEDIDO_ITEM")
    private String orderItem;

    @Column(name = "UN_NUMERO_RESERVA")
    private String reservationNumber;

    @Column(name = "UN_RESERVA_ITEM")
    private String reservationItem;

    @Column(name = "UN_NUMERO_IMOBILIZADO")
    private String fixedNumber;

    @Column(name = "UN_SUB_NUMERO_IMOBILIZADO")
    private String fixedSubnumber;

    @Column(name = "UN_MOTIVO_BAIXA")
    private String reasonForWriteOff;

    @Column(name = "UN_NUM_BO")
    private String boNumber;

    @Column(name = "UN_LAUDO_TECNICO")
    private String technicalReport;

    @Column(name = "UN_NUMERO_SINISTRO")
    private String sinisterNumber;

    @Column(name = "UN_UF_ORIGEM")
    private String originUf;

    @Column(name = "UN_MOTIVO_CADASTRO")
    private String registerReason;

    @Column(name = "UN_MOTIVO_INSTALACAO")
    private String instalationReason;

    @Column(name = "UN_STATUS_SAP")
    private String sapStatus;

    @Column(name = "UN_MENSAGEM_SAP")
    private String sapMesage;

    @Column(name = "UN_EMP_CONTABIL")
    private String accountantCompany;

    @Column(name = "UN_DATA_BAIXA_CONTABILIDADE")
    private LocalDateTime accountantDischargeDate;

    @Column(name = "UN_DATA_BAIXA_LOGISTICA")
    private LocalDateTime logisticsDischargeDate;

    @Column(name = "UN_COD_BARRA_SWAP")
    private String swapBarcode;

    @Column(name = "UN_CLASSE_ATIVO")
    private String activeClass;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "unity")
    private List<RepairTicket> tickets;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.unity")
    private List<ElectricalPropUnity> electricalProperties;

    public boolean isSomeOrderSetFilled(){
        return (getOrderItem()!=null || getOrderNumber()!=null);
    }

    public boolean isSomeFixedSetFilled(){
        return (getFixedNumber()!=null || getFixedSubnumber()!=null);
    }

    public boolean isSomeResevationSetFilled(){
        return (getReservationItem()!=null || getReservationNumber()!=null);
    }

    public boolean isOrderSetTotallyFilled(){
        return (getOrderItem()!=null && getOrderNumber()!=null);
    }

    public boolean isFixedSetTotallyFilled(){
        return (getFixedNumber()!=null && getFixedSubnumber()!=null);
    }

    public boolean isResevationSetTotallyFilled(){
        return (getReservationItem()!=null && getReservationNumber()!=null);
    }
    @Transient
    private String descriptionProcedure;

    public String getDescriptionProcedure() {
        StringBuilder description = new StringBuilder();

        description.append(String.format("Código de Barras: %s %n", id))
                .append(String.format("Código da Unidade/Descrição: %s - %s %n", unityCode.getId(), unityCode.getDescription()))
                .append(String.format("Situação: %s - %s %n",situationCode.getId(), situationCode.getDescription()));

        if((SituationEnum.onRepair().stream().anyMatch(x -> x.getCod().equals(this.situationCode.getId()))))
            addTicketDescription(description);

        description.append(String.format("Proprietário: %s %n", responsible!=null?responsible.getId():"" ))
                .append(String.format("Estação: %s %n", station!=null?station.getId():"" ))
                .append(String.format("Localização: %s %n", location!=null?location:"" ))
                .append(String.format("Empresa Contábil/Classe de Ativo : %s%s%s %n", accountantCompany!=null?accountantCompany:"", accountantCompany==null&&activeClass==null?"":"/", activeClass!=null?activeClass:"" ));

        return description.toString();
    }

    private void addTicketDescription(StringBuilder description) {
        Optional<RepairTicket> ticket = Optional.empty();
        if(this.tickets != null && !this.tickets.isEmpty()){
           ticket = this.tickets.parallelStream().filter(t-> t.getClosureDate()==null).findFirst();
        }
        ticket.ifPresent(repairTicket -> description.append(String.format("Número do BR: %s %n", repairTicket.getId())));
    }
}
