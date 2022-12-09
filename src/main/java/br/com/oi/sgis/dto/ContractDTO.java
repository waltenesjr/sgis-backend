package br.com.oi.sgis.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Builder
@Data
public class ContractDTO {
    @NotBlank(message = "O código deve ser informada")
    @Size(max = 30,message = "O código deve possuir no máximo 30 caracteres")
    private String id;
    @NotNull(message = "A empresa deve ser informada")
    private CompanyDTO company;
    @NotNull(message = "O gestor deve ser informado")
    private DepartmentDTO department;
    private String extensiveDescription;
    @Digits(integer = 7, fraction = 2, message = "O valor deve possuir o formato 9999999.99, com no máximo 7 casas inteiras e 2 casas decimais")
    private BigDecimal value;
    private Integer amount;
    private BigDecimal realizedValue;
    @Digits(integer = 7, fraction = 2, message = "O valor deve possuir o formato 9999999.99, com no máximo 7 casas inteiras e 2 casas decimais")
    private Integer realizedAmount;
    private boolean acquisition;
    private boolean maintenance;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message = "A data inicial deve ser informada")
    private LocalDateTime initialDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message = "A data final deve ser informada")
    private LocalDateTime finalDate;
    @NotNull(message = "Os dias de garantia devem ser informados")
    private Integer warrantyDays;
    private Integer deliveryDays;
    @Size(max = 60,message = "A descrição deve possuir no máximo 60 caracteres")
    private String description;
    private LocalDateTime cancelDate;
    private List<ModelContracDTO> modelContracts;


    @JsonIgnore
    public String getDateReport(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return initialDate.format(formatter) + " - " + finalDate.format(formatter);
    }
}
