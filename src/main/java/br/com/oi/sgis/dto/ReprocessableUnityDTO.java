package br.com.oi.sgis.dto;

import br.com.oi.sgis.enums.RegisterReasonEnum;
import br.com.oi.sgis.enums.SapReprocessConditionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReprocessableUnityDTO {
    private Long idTbrec;
    private Integer canBeReprocessed;
    private String operation;
    private String reasonForWriteOff;
    private String registerReason;
    private String unityId;
    private String unityModelId;
    private String serieNumber;
    private String orderNumber;
    private String orderItem;
    private String reservationNumber;
    private String reservationItem;
    private String fixedNumber;
    private String fixedSubNumber;
    private String responsibleId;
    private String situation;
    private String originUf;
    private String accountantCompany;
    private String activeClass;

    public boolean isFirstCondition(){
       return this.operation.equals("I") && (registerReason!=null && registerReason.equals(RegisterReasonEnum.CUS.getReason()));
    }

    public boolean isSecondCondition(){
        return this.operation.equals("M") && (registerReason!=null && registerReason.equals(RegisterReasonEnum.CTR.getReason()));
    }

    public boolean isThirdCondition(){
        return this.operation.equals("B") || (this.operation.equals("M") && registerReason == null);
    }

    public SapReprocessConditionEnum getCondition(){
        if(isFirstCondition()) return SapReprocessConditionEnum.FIRST;
        if(isSecondCondition()) return SapReprocessConditionEnum.SECOND;
        if(isThirdCondition()) return SapReprocessConditionEnum.THIRD;
        return SapReprocessConditionEnum.NONE;
    }
}
