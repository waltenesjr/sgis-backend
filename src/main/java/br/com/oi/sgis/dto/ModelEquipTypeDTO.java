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

@Builder @Data @NoArgsConstructor @AllArgsConstructor
public class ModelEquipTypeDTO {
    @NotBlank(message = "O código do modelo de equipamentos não deve ser nulo ou em branco. ")
    @Size(max = 40,message = "O código do modelo de equipamentos deve possuir no máximo 40 caracteres")
    private String id;
    @NotBlank(message = "A descrição do modelo de equipamentos não deve ser nula ou em branco. ")
    @Size(max = 120,message = "A descrição do modelo de equipamentos deve possuir no máximo 120 caracteres")
    private String description;
    @NotBlank(message = "O mnemônico do modelo de equipamentos não deve ser nulo ou em branco. ")
    @Size(max = 60,message = "O mnemônico do modelo de equipamentos deve possuir no máximo 60 caracteres")
    private String mnemonic;
    @NotNull(message = "O fabricante deve ser informado.")
    private CompanyDTO company;
    @NotNull(message = "O tipo deve ser informado.")
    private EquipamentTypeDTO equipamentType;
    private TechnicalStaffDTO technician;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date;
    @NotNull(message = "A tecnologia deve ser informada.")
    private TechnologyDTO technology;
    private String costCenter;
    @Size(max = 10,message = "A conta SAP do modelo de equipamentos deve possuir no máximo 10 caracteres")
    private String accountSap;
    @NotBlank(message = "A área técnica do modelo de equipamentos não deve ser nula ou em branco. ")
    @Size(max = 10,message = "A área técnica do modelo de equipamentos deve possuir no máximo 10 caracteres")
    private String techniqueCode;
    private Boolean descountFlag;

}
