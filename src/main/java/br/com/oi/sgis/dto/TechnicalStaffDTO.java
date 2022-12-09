package br.com.oi.sgis.dto;

import br.com.oi.sgis.entity.Level;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TechnicalStaffDTO {

    @NotBlank(message = "A matrícula não deve ser nula")
    @Size(max = 10,message = "A matrícula deve possuir no máximo 10 caracteres")
    private String id;

    @NotBlank(message = "O nome do técnico não deve ser nulo")
    @Size(max = 60,message = "O nome do técnico deve possuir no máximo 60 caracteres")
    private String technicianName;

    @NotBlank(message = "O telefone deve ser informado")
    @Size(max = 20,message = "O telefone deve possuir no máximo 20 caracteres")
    private String techPhoneBase;

    @Size(max = 20,message = "O telefone residencial deve possuir no máximo 60 caracteres")
    private String techPhoneResid;

    @NotBlank(message = "O e-mail deve ser informado")
    @Size(max = 50,message = "O e-mail deve possuir no máximo 50 caracteres")
    private String email;

    @NotNull(message = "A Lotatção deve ser informada")
    private DepartmentDTO departmentCode;

    @NotBlank(message = "O nome do gerente deve ser informado")
    @Size(max = 60,message = "O nome do gerente deve possuir no máximo 60 caracteres")
    private String managerName;

    private String companyName;
    @NotNull(message = "Empresa deve ser informada")
    private ParameterDTO cgcCpfCompany;

    private boolean active;

    private boolean repairFlag;

    private Set<Level> levels;

    private BigDecimal manHourValue;


}
