package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepairTicketDTO {

    @NotBlank(message = "A unidade não deve ser nula")
    @Size(max = 40,message = "O código de barras deve possuir no máximo 40 caracteres")
    private String unityId;

    @NotNull(message = "O defeito não deve ser nulo")
    private DefectDTO defect;

    @Size(max = 250,message = "O detalhamento do defeito deve possuir no máximo 250 caracteres")
    private String description;

    @Size(max = 250,message = "A observação deve possuir no máximo 250 caracteres")
    private String observation;

    private SituationDTO situation;

    @Size(max = 15,message = "O número FAS deve possuir no máximo 15 caracteres")
    private String fasNumber;

    private boolean generatePendency;

    @Size(max = 20,message = "O número de BR deve possuir no máximo 20 caracteres")
    private String brNumber;

    @NotBlank(message = "O centro de reparo não deve ser nulo")
    @Size(max = 20,message = "O código do departamento de reparo deve possuir no máximo 20 caracteres")
    private String repairCenterDepartment;

    @NotBlank(message = "O departamento de origem não deve ser nulo")
    @Size(max = 20,message = "O código do departamento de origem deve possuir no máximo 20 caracteres")
    private String originDepartment;

    @NotBlank(message = "O departamento de devolução não deve ser nulo")
    @Size(max = 20,message = "O código do departamento de devolução deve possuir no máximo 20 caracteres")
    private String devolutionDepartment;

    @NotNull(message = "A data de fechamento BA/BD não deve ser nula")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime normalizationDate;

    @NotBlank(message = "O técnico não deve ser nulo")
    @Size(max = 10,message = "O código do técnico responsável deve possuir no máximo 10 caracteres")
    private String technician;

    @Size(max = 10,message = "O código do técnico operador deve possuir no máximo 10 caracteres")
    private String operator;

    @NotBlank(message = "A estação de origem não deve ser nula")
    @Size(max = 10,message = "O código da estação deve possuir no máximo 10 caracteres")
    private String station;

    private CentralDTO grouping;

    @NotNull(message = "A prioridade não deve ser nula")
    private PriorityRepairDTO priority;

    @NotBlank(message = "O número BA/BD não deve ser nulo")
    @Size(max = 20,message = "O número BA/BD  deve possuir no máximo 20 caracteres")
    private String baNumber;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime prevision;

    @NotNull(message = "A data de entrega CSP/PS não deve ser nula")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime cspCsDeliverDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime openDate;

    private CompanyDTO maintainer;
    private TechnicalStaffDTO repairTechnician;
    private ContractDTO contract;
    private UnityDTO unity;

}
