package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {

    @NotBlank(message = "A sigla deve ser informada")
    @Size(max = 20,message = "A sigla deve possuir no máximo 20 caracteres")
    private String id;

    @Size(max = 20,message = "A diretoria deve possuir no máximo 20 caracteres")
    private String directorship;

    private String managerName;

    private String managerRegisterNum;

    private String contactName;

    private String contactRegisterNum;

    @Size(max = 20,message = "O centro responsável deve possuir no máximo 20 caracteres")
    private String responsibleCenter;

    @Size(max = 20,message = "O centro default deve possuir no máximo 20 caracteres")
    private String repairCenterDefault;

    @Size(max = 15,message = "O telefone deve possuir no máximo 15 caracteres")
    private String phone;

    private AddressDTO address;

    @Size(max = 120,message = "A descrição deve possuir no máximo 120 caracteres")
    private String description;

    @Size(max = 20,message = "O centro de custo deve possuir no máximo 20 caracteres")
    private String costCenter;

    @NotNull
    private boolean inactive;

    private boolean spareCenter;

    private boolean systemFlag;

    private boolean obligateUse;

    private boolean repairCenter;

    private boolean pendDef;

    private boolean contractBudgetAnalysis;

    private boolean unanalyzedLock;

    private boolean unscreenedBlock;

    private boolean notDesignatedBloq;

    private boolean obligateFowarding;

}
