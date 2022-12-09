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
import java.time.format.DateTimeFormatter;


@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "V_MOV_ITENS")
public class MovItensView {
    @Id
    private Long id;
    @Column(name = "DATA")
    private LocalDateTime date;
    @Column(name = "COD_BARRAS")
    private String barcode;
    @Column(name = "PARA_COD_PLACA")
    private String unityCode;
    @Column(name = "MNEMONICO")
    private String mnemonic;
    @Column(name = "DESCRICAO")
    private String description;
    @Column(name = "DE_COD_SIT")
    private String fromSituationCode;
    @Column(name = "PARA_COD_SIT")
    private String toSituationCode;
    @Column(name = "DE_SIGLA_PROP")
    private String fromResponsible;
    @Column(name = "PARA_SIGLA_PROP")
    private String toResponsible;
    @Column(name = "DE_TECNICO")
    private String fromTechnician;
    @Column(name = "PARA_TECNICO")
    private String toTechnician;


    public String getDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }
}
