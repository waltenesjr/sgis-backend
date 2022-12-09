package br.com.oi.sgis.entity;

import com.google.common.base.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ORCAMENTO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Estimate implements Serializable {

    private static final long serialVersionUID = 8823396342152265866L;

    @Id
    @Column(name = "OR_NUM_ORC", nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "OR_CGC_CPF_ORC", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "OR_COD_CONTRATO", nullable = false)
    private Contract contract;

    @Column(name = "OR_NUM_EMPRESA")
    private String companyNumber;

    @ManyToOne
    @JoinColumn(name = "OR_TECNICO", nullable = false)
    private TechnicalStaff technician;

    @Column(name = "OR_VALOR")
    private BigDecimal value;

    @Column(name = "OR_DATA_ORC")
    private LocalDateTime date;

    @Column(name = "OR_DATA_VALIDADE")
    private LocalDateTime expirationDate;

    @Column(name = "OR_DATA_ENVIO")
    private LocalDateTime sendDate;

    @Column(name = "OR_DATA_APROV")
    private LocalDateTime approvalDate;

    @Column(name = "OR_NOTA_FISCAL")
    private String fiscalNote;

    @Column(name = "OR_DATA_NF")
    private LocalDateTime fiscalNoteDate;

    @Column(name = "OR_DATA_RETORNO")
    private LocalDateTime returnDate;

    @Column(name = "OR_DATA_GARANTIA")
    private LocalDateTime warrantyDate;

    @Column(name = "OR_OBSERVACOES")
    private String observation;

    @Column(name = "OR_TELEFONE")
    private String phone;

    @Column(name = "OR_CONTATO")
    private String contact;

    @Column(name = "OR_SITUACAO")
    private String situation;

    @Column(name = "OR_DATA_PREV")
    private LocalDateTime previsionDate;

    @Column(name = "OR_FLAG_IMPR")
    private Long flagImpr;

    @Column(name = "OR_DATA_CANCEL")
    private LocalDateTime cancelDate;

    @Column(name = "OR_DIAS_ENTREGA")
    private Long deliveryDays;

    @Column(name = "OR_DIAS_GARANTIA")
    private Long warrantyDays;

    @ManyToOne
    @JoinColumn(name = "OR_SIGLA", nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "OR_ENDERECO")
    private Address address;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.estimate")
    private List<ItemEstimate> itemEstimates;

    @Transient
    private String procedureDescription;

    @Transient
    private Integer volume;

    @Transient
    private BigDecimal refValue;

    @Transient
    private BigDecimal valueItems;

    public Integer getVolume() {
        if(itemEstimates==null)
            return 0;
        return itemEstimates.size();
    }

    public BigDecimal getRefValue() {
        if(itemEstimates==null)
            return BigDecimal.ZERO;
        return itemEstimates.stream().filter(i -> i.getId().getTicketIntervention().getUnity().getUnityCode().getValue() != null)
                .map(i -> i.getId().getTicketIntervention().getUnity().getUnityCode().getValue()).reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getValueItems() {
        if(itemEstimates==null)
            return BigDecimal.ZERO;
        return itemEstimates.stream().filter(i -> i.getValue() !=null).map(ItemEstimate::getValue).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public String getProcedureDescription() {
        StringBuilder description = new StringBuilder();

        description.append(String.format("Orçamento: %s %n", id))
                .append(String.format("Empresa: %s - %s %n", company.getCnpjCpf(), company.getTradeName()))
                .append(String.format("Técnico: %s - %s %n", technician.getId(), technician.getTechnicianName()));
        if(contract!=null)
            description.append(String.format("Contrato: %s %n",contract.getId()));
        description.append(String.format("Valor: %s %n",value!=null ? value : BigDecimal.ZERO));

        return description.toString();
    }

    @Override
    public String toString() {
        return "Estimate{" +
                "id='" + id + '\'' +
                '}';
    }
}
