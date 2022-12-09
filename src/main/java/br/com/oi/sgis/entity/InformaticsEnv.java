package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "TBENV_INFORMATICA")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InformaticsEnv implements Serializable {

    private static final long serialVersionUID = -4549141488136361270L;
    @Id
    @Column(name = "ID_UNICO_INTERFACE", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_EQUIPAMENTO", nullable = false)
    private Unity unity;

    @Column(name = "OPERACAO", nullable = false, length = 4)
    private String operation;

    @Column(name = "DATA_EVENTO", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "DATA_LEITURA_INF")
    private LocalDateTime infoReadingDate;

    @Column(name = "ID_SAP_PM", length = 18)
    private String sapId;

    @Column(name = "ID_SAP_PM_SUBST", length = 18)
    private String substSapId;

    @Column(name = "ID_PM_REFERENCIA", length = 18)
    private String referencePmId;

    @ManyToOne
    @JoinColumn(name = "ID_EQUIPAMENTO_INVENTARIO")
    private Unity inventoryUnity;

    @Column(name = "CLASSE_ATIVO", length = 10)
    private String activeClass;

    @ManyToOne
    @JoinColumn(name = "MODELO", nullable = false)
    private ModelEquipamentType model;

    @ManyToOne
    @JoinColumn(name = "FABRICANTE", nullable = false)
    private Manufacturer manufacturer;

    @Column(name = "NUMERO_SERIE", length = 50)
    private String serieNumber;

    @Column(name = "PART_NUMBER", length = 50)
    private String partNumber;

    @Column(name = "STATUS", length = 4)
    private String status;

    @Column(name = "NUMERO_PEDIDO", length = 10)
    private String orderNumber;

    @Column(name = "NUM_PEDIDO_ITEM", length = 5)
    private String orderItem;

    @Column(name = "NUMERO_RESERVA", length = 10)
    private String reservationNumber;

    @Column(name = "RESERVA_ITEM", length = 4)
    private String reservationItem;

    @Column(name = "NUMERO_IMOBILIZADO", length = 12)
    private String fixedNumber;

    @Column(name = "SUB_NUMERO_IMOBILIZADO", length = 4)
    private String fixedSubnumber;

    @Column(name = "MOTIVO_BAIXA", length = 2)
    private String reasonForWriteOff;

    @Column(name = "UF", length = 2)
    private String uf;

    @Column(name = "LOCALIDADE", length = 4)
    private String location;

    @Column(name = "ESTACAO", length = 10)
    private String station;

    @Column(name = "ANDAR", length = 40)
    private String floor;

    @Column(name = "SALA", length = 25)
    private String room;

    @Column(name = "FILA_LADO", length = 15)
    private String queueNext;

    @Column(name = "POSICAO", length = 15)
    private String position;

    @Column(name = "RACK", length = 7)
    private String rack;

    @Column(name = "SHELF", length = 12)
    private String shelf;

    @Column(name = "SLOT_INSTALADO", length = 30)
    private String installedSlot;

    @Column(name = "SLOT_RETIRADO", length = 30)
    private String removedSlot;

    @Column(name = "ID_CENTRO_SOBRESSALENTE", length = 20)
    private String spareCenterId;

    @Column(name = "NUM_BO", length = 30)
    private String boNumber;

    @Column(name = "LAUDO_TECNICO", length = 30)
    private String technicalReport;

    @Column(name = "NUMERO_SINISTRO", length = 30)
    private String sinisterNumber;

    @Column(name = "MOTIVO_CADASTRO", length = 3)
    private String registerReason;

    @Column(name = "EMPRESA_CONTABIL", length = 4)
    private String accountantCompany;

    @Column(name = "FABRICANTE_SWAP", length = 60)
    private String swapManufacturer;

    @Column(name = "MODELO_SWAP", length = 50)
    private String swapModel;
}
