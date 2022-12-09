package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {

    private String id;
    @NotBlank(message = "O CNPJ/CPF deve ser informado")
    @Size(max = 14,message = "O CNPJ/CPF deve possuir no máximo 14 caracteres")
    private String cnpjCpf;
    @NotBlank(message = "A razão social deve ser informada")
    @Size(max = 120,message = "A razão social deve possuir no máximo 120 caracteres")
    private String companyName;
    @NotBlank(message = "O nome fantasia deve ser informado")
    @Size(max = 60,message = "O nome fantasia deve possuir no máximo 60 caracteres")
    private String tradeName;
    @NotBlank(message = "A inscrição estadual deve ser informada")
    @Size(max = 25,message = "A inscrição estadual deve possuir no máximo 25 caracteres")
    private String stateRegistration;
    private boolean client;
    private boolean provider;
    private boolean manufacturer;
    private boolean holding;
    private boolean active;
    private String eanCode;
    private String sapAbbrev;
    private ManufacturerDTO manufacturerCode;
    private List<AddressDTO> addresses;

}
