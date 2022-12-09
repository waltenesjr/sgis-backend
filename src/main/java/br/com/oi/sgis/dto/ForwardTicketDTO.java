package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForwardTicketDTO {

    private String originId;
    private String unityId;
    private String barcode;
    private String situation;
    private LocalDateTime initialDate;
    private LocalDateTime finalDate;
    private String technicianId;
    private String contractId;
    private String maintainerId;
}
