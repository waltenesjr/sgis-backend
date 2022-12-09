package br.com.oi.sgis.dto;

import br.com.oi.sgis.entity.Uf;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private String id;
    private CompanyDTO cgcCpf;
    @Size(max = 20,message = "O descrição deve possuir no máximo 20 caracteres")
    private String description;
    @NotNull(message = "A uf não deve ser nula")
    private Uf uf;
    @NotBlank(message = "O campo endereço não deve ser nulo")
    @Size(max = 250,message = "O campo endereço deve possuir no máximo 250 caracteres")
    private String addressDescription;
    @NotBlank(message = "O campo bairro não deve ser nulo")
    @Size(max = 30,message = "O campo bairro deve possuir no máximo 30 caracteres")
    private String district;
    @NotBlank(message = "O campo cidade não deve ser nulo")
    @Size(max = 30,message = "O campo cidade deve possuir no máximo 30 caracteres")
    private String city;
    @NotBlank(message = "O CEP não deve ser nulo")
    @Size(max = 10,message = "O CEP deve possuir no máximo 10 caracteres")
    private String cep;
    @NotBlank(message = "O telefone não deve ser nulo")
    @Size(max = 15,message = "O telefone deve possuir no máximo 15 caracteres")
    private String phone;
    @Size(max = 15,message = "O fax deve possuir no máximo 15 caracteres")
    private String fax;
    @Size(max = 15,message = "O contato deve possuir no máximo 30 caracteres")
    private String contact;
}
