package br.com.oi.sgis.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParameterDTO {

    @NotBlank(message = "O CGC/CPF não deve ser nulo")
    @Size(max = 14,message = "O CGC/CPF devem possuir no máximo 14 caracteres")
    private String id;
    @NotNull(message = "A empresa não deve ser nula")
    private CompanyDTO company;
    @NotNull(message = "A versão não deve ser nula")
    private BigDecimal version;
    @NotBlank(message = "O prefixo não deve ser nula")
    @Size(max = 2, min = 2,message = "O prefixo deve possuir exatamente 2 caracteres" )
    private String prefix;
    @NotBlank(message = "A sigla não deve ser nula")
    @Size(max = 2, min = 2,message = "A sigla deve possuir no exatamente 2 caracteres")
    private String abbreviation;
    private BigDecimal manHour;
    @Size(max = 20,message = "O código de barras deve possuir no máximo 20 caracteres")
    private String barcode;
    @Size(max = 20,message = "O código de caixa deve possuir no máximo 20 caracteres")
    private String boxCode;
    @Size(max = 2048,message = "A mensagem deve possuir no máximo 2048 caracteres")
    private String message;
    private BigDecimal timeout;
    private String ftp;








}
