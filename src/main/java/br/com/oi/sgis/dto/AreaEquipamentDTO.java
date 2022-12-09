package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AreaEquipamentDTO {

    @NotBlank(message = "O código deve ser informado")
    @Size(max = 40,message = "O código deve possuir no máximo 40 caracteres")
    private String id;
    @NotBlank(message = "O mnemônico deve ser informado")
    @Size(max = 60,message = "O mnemônico deve possuir no máximo 60 caracteres")
    private String mnemonic;
    @NotBlank(message = "A descrição deve ser informada")
    @Size(max = 120,message = "A descrição deve possuir no máximo 120 caracteres")
    private String description;
    @NotBlank(message = "A descrição abreviada deve ser informado")
    @Size(max = 120,message = "A descrição abreviada deve possuir no máximo 120 caracteres")
    private String abrevDescription;
    @NotNull(message = "O modelo deve ser informado")
    private ModelEquipTypeDTO equipModelCode;
    @NotNull(message = "O valor deve ser informado")
    @Digits(integer = 10, fraction = 2, message = "O valor deve possuir o formato 999999999.99, com no máximo 10 casas inteiras e 2 casas decimais")
    private BigDecimal value;
    @Digits(integer = 10, fraction = 2, message = "O peso deve possuir o formato 999999999.99, com no máximo 10 casas inteiras e 2 casas decimais")
    private BigDecimal weight;
    private Integer plaqueQuantity;
    private CompanyDTO company;
    private LocalDateTime date;
    @Size(max = 30,message = "O código de barras deve possuir no máximo 30 caracteres")
    private String barcode;
    private Integer adjustDays;
    @Size(max = 10,message = "A classificação fiscal deve possuir no máximo 10 caracteres")
    private String fiscalClassification;
    private TechnicalStaffDTO technician;
    @Size(max = 10,message = "A área técnica deve possuir no máximo 10 caracteres")
    private String techniqueCode;
    private Boolean discontinuedFlag;
    private List<ElectricalPropEquipDTO> electricalProperties;
}
