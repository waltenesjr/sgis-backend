package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Builder
@AllArgsConstructor
@Getter
public class RepairSummaryValueDTO {
    private int month;
    private int year;
    private String repairCenter;
    private BigDecimal con;
    private BigDecimal scr;
    private BigDecimal sde;
    private BigDecimal aju;
    private BigDecimal gar;
    private BigDecimal orc;
    private BigDecimal ctr;
    private BigDecimal gct;
    private BigDecimal gor;
    private BigDecimal sce;
    @Setter
    private int open;

    public BigDecimal getCon() {
        return getBigdecimal(con);
    }

    public BigDecimal getScr() {
        return getBigdecimal(scr);
    }

    public BigDecimal getSde() {
        return getBigdecimal(sde);
    }

    public BigDecimal getAju() {
        return getBigdecimal(aju);
    }

    public BigDecimal getGar() {
        return getBigdecimal(gar);
    }

    public BigDecimal getOrc() {
        return getBigdecimal(orc);
    }

    public BigDecimal getCtr() {
        return getBigdecimal(ctr);
    }

    public BigDecimal getGct() {
        return getBigdecimal(gct);
    }

    public BigDecimal getGor() {
        return getBigdecimal(gor);
    }

    public BigDecimal getSce() {
        return getBigdecimal(sce);
    }

    private BigDecimal getBigdecimal(BigDecimal value) {
        if(value == null)
            return BigDecimal.ZERO;
        return value;
    }

    public int getMonthInt() {
        return month;
    }

    public String getMonth() {
        DecimalFormat df = new DecimalFormat("00");
        return df.format(month);
    }

    public BigDecimal getRiTotal(){
        return getCon().add(getSde()).add(getScr()).add(getAju());
    }

    public BigDecimal getReTotal(){
        return getGct().add(getOrc()).add(getCtr()).add(getGor()).add(getSce()).add(getGar());
    }

    public BigDecimal getTotal(){
        return getRiTotal().add(getReTotal());
    }

    public BigDecimal getRiPercentage(){
        if(getTotal().intValue() == 0)
            return BigDecimal.ZERO;
        return (getRiTotal().divide(getTotal(),2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100));
    }

    public BigDecimal getRePercentage(){
        if(getTotal().intValue() == 0)
            return BigDecimal.ZERO;
        return (getReTotal().divide(getTotal(),2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100));

    }
}
