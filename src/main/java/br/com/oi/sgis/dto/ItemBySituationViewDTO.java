package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemBySituationViewDTO {
    private Long trn;
    private Long dis;
    private Long emp;
    private Long tdr;
    private Long emu;
    private Long trr;
    private Long bxp;
    private Long trp;
    private Long tre;
    private Long rep;
    private Long bxu;
    private Long bxe;
    private Long dvr;
    private Long trd;
    private Long uso;
    private Long bxi;
    private Long pre;
    private Long bxc;
    private Long bxo;
    private Long res;
    private Long ofe;
    private Long def;
    private Long tdd;
    private String codUnity;
    private String departmentCode;
    private Long total;

    public Long getTotal() {
        return  trn + dis + emp + tdr + emu + trr + trp + tre + rep + bxu + bxe + dvr + trd +
                uso + bxi + pre + bxc + bxo + res + ofe + def + tdd;
    }
}
