package br.com.oi.sgis.dto;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.math.BigDecimal;

@Builder
@Getter @Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class EquipamentTypeRepairDTO {
    private String repairCenter;
    private String equipment;
    private String description;
    private Integer ri;
    private Integer normalRe;
    private Integer warrantyRe;
    private BigDecimal timeRi;
    private BigDecimal timeRe;
    private BigDecimal percRi;
    private BigDecimal percRe;
    private BigDecimal valueUnityRi;
    private BigDecimal valueUnityRe;
    private BigDecimal valueRi;
    private BigDecimal valueRe;
    private Integer openNotAccepted;
    private Integer openNotForward;
    private Integer openRi;
    private Integer openReGar;
    private Integer openRe;
    private String id;
    @Transient
    private Integer total;
    @Transient
    private Integer totalPend;
    public void setId(String id) {
        this.id = id;
    }

    @Id
    public String getId() {
        return id;
    }

    @Transient
    public Integer getTotal(){
        return ri + normalRe + warrantyRe;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void setTotalPend(Integer totalPend) {
        this.totalPend = totalPend;
    }

    @Transient
    public Integer getTotalPend(){
        return openNotAccepted + openNotForward + openRi + openReGar + openRe;
    }

    public BigDecimal getTimeRi() {
        return getBigDecimal(timeRi);
    }

    private BigDecimal getBigDecimal(BigDecimal value) {
        if(value == null)
            return BigDecimal.ZERO;
        return value;
    }

    public BigDecimal getTimeRe() {
        return getBigDecimal(timeRe);
    }

    public BigDecimal getPercRi() {
        return getBigDecimal(percRi);
    }

    public BigDecimal getPercRe() {
        return getBigDecimal(percRe);
    }
}
