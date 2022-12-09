package br.com.oi.sgis.dto;

import br.com.oi.sgis.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReprocessSapDTO {
    private String userId;
    private String unityId;
    private String unityCode;
    private String orderNumber;
    private String orderItem;
    private String fixedNumber;
    private String fixedSubnumber;
    private String reservationItem;
    private String reservationNumber;
    private String serieNumber;
    private String responsibleId;
    private String originUf;
    private String operation;
    private String registerReason;
    private Long idInformaticsRec;


    public static ReprocessSapDTO fromSapReprocessUnity(UnityToReprocessSapDTO unityToReprocessSapDTO) {
       return builder().userId(Utils.getUser().getId())
                .unityId(unityToReprocessSapDTO.getUnityId())
                .unityCode(unityToReprocessSapDTO.getUnityCode())
                .orderNumber(unityToReprocessSapDTO.getOrderNumber())
                .orderItem(unityToReprocessSapDTO.getOrderItem())
                .fixedNumber(unityToReprocessSapDTO.getFixedNumber())
                .fixedSubnumber(unityToReprocessSapDTO.getFixedSubnumber())
                .reservationItem(unityToReprocessSapDTO.getReservationItem())
                .reservationNumber(unityToReprocessSapDTO.getReservationNumber())
                .serieNumber(unityToReprocessSapDTO.getSerieNumber())
                .responsibleId(unityToReprocessSapDTO.getResponsible())
                .originUf(unityToReprocessSapDTO.getOriginUf())
                .build();
    }
}
