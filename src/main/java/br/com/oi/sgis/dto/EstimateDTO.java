package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstimateDTO {

    private String id;
    @NotNull(message = "A empresa deve ser informada")
    private CompanyDTO company;
    private ContractDTO contract;
    private String companyNumber;
    private TechnicalStaffDTO technician;
    private BigDecimal value;
    @NotNull(message = "Data de orçamento deve ser informada")
    private LocalDateTime date;
    private LocalDateTime expirationDate;
    private LocalDateTime sendDate;
    private LocalDateTime approvalDate;
    private String fiscalNote;
    private LocalDateTime fiscalNoteDate;
    private LocalDateTime returnDate;
    private LocalDateTime warrantyDate;
    private String observation;
    private String phone;
    private String contact;
    private String situation;
    private LocalDateTime previsionDate;
    private Long flagImpr;
    private LocalDateTime cancelDate;
    private Long deliveryDays;
    private Long warrantyDays;
    private DepartmentDTO department;
    private AddressDTO address;
    private List<ItemEstimateDTO> itemEstimates;
    private String procedureDescription;
    private Integer volume;
    private BigDecimal refValue;
    private BigDecimal valueItems;
}
