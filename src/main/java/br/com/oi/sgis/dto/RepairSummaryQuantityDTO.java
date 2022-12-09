package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Builder @AllArgsConstructor @Getter
public class RepairSummaryQuantityDTO {
    private int month;
    private int year;
    private String repairCenter;
    private long con;
    private long scr;
    private long sde;
    private long aju;
    private long gar;
    private long orc;
    private long ctr;
    private long gct;
    private long gor;
    private long sce;
    @Setter
    private int open;


    public int getMonthInt() {
        return month;
    }

    public String getMonth() {
        DecimalFormat df = new DecimalFormat("00");
        return df.format(month);
    }

    public long getRiTotal(){
        return con + sde + scr + aju;
    }

    public long getReTotal(){
        return gct + orc + ctr + gor + sce + gar;
    }

    public long getTotal(){
        return getRiTotal() + getReTotal();
    }

    public BigDecimal getRiPercentage(){
        if(getTotal() == 0)
            return BigDecimal.ZERO;
        return (BigDecimal.valueOf(getRiTotal()).divide(BigDecimal.valueOf(getTotal()),2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
    }

    public BigDecimal getRePercentage(){
        if(getTotal() == 0)
            return BigDecimal.ZERO;
        return (BigDecimal.valueOf(getReTotal()).divide(BigDecimal.valueOf(getTotal()),2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));

    }

}
