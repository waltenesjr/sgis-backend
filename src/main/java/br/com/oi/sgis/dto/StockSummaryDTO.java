package br.com.oi.sgis.dto;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Builder
@Getter @Setter @Entity
@NoArgsConstructor @AllArgsConstructor
public class StockSummaryDTO {

    private String responsible;
    private String unity;
    private Boolean inactive;
    private String description;
    private Long min;
    private Long repos;
    private Long max;
    private String location;
    private String station;
    private Integer trn;
    private Integer dis;
    private Integer emp;
    private Integer tdr;
    private Integer emu;
    private Integer trr;
    private Integer bxp;
    private Integer trp;
    private Integer tre;
    private Integer rep;
    private Integer bxu;
    private Integer bxe;
    private Integer dvr;
    private Integer trd;
    private Integer uso;
    private Integer bxi;
    private Integer pre;
    private Integer bxc;
    private Integer bxo;
    private Integer res;
    private Integer ofe;
    private Integer def;
    private Integer tdd;
    private Integer disponible;
    private Integer reposition;
    private Integer off;
    private String id;

    public Integer getDisponible() {
        return dis + emp + emu + res + trd + tre + uso;
    }

    public Integer getReposition() {
        return def + dvr + rep + tdr + dvr + trr ;
    }

    public Integer getOff() {
        return ofe + pre + trn + trp ;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Id
    public String getId() {
        return id;
    }
}
