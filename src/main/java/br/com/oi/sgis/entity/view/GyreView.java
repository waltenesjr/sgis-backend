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
@Table(name = "V_UN_GIRO2 ")
public class GyreView {
    @Id
    private Long id;
    @Column(name = "COD_BARRAS")
    private String unityId;
    @Column(name = "DATA")
    private LocalDateTime date;
    @Column(name = "PARA_COD_PLACA")
    private String toUnityCode;
    @Column(name = "DE_COD_SIT")
    private String fromSituation;
    @Column(name = "PARA_COD_SIT")
    private String toSituation;
    @Column(name = "DE_SIGLA_PROP")
    private String fromResponsible;
    @Column(name = "PARA_SIGLA_PROP")
    private String toResponsible;
    @Column(name = "DE_TECNICO")
    private String fromTechnician;
    @Column(name = "PARA_TECNICO")
    private String toTechnician;

}
