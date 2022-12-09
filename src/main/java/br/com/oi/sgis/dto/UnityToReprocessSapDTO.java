package br.com.oi.sgis.dto;

import br.com.oi.sgis.annotations.NotBlankOrNull;
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
public class UnityToReprocessSapDTO {
    @NotBlank(message = "O código de unidade deve ser informado")
    private String unityId;

    @NotBlank(message = "O número de série não deve ser nulo")
    @Size(max = 30,message = "O número de série deve possuir no máximo 30 caracteres")
    private String serieNumber;

    @NotBlankOrNull(message = "O número item não deve ser informado em branco")
    @Size(max = 10,message = "O número item deve possuir no máximo 10 caracteres")
    private String orderNumber;

    @NotBlankOrNull(message = "O pedido item não deve ser informado em branco")
    @Size(max = 5,message = "O pedido item deve possuir no máximo 5 caracteres")
    private String orderItem;

    @NotBlankOrNull(message = "O número imobilizado não deve ser informado em branco")
    @Size(max = 12,message = "O número imobilizado deve possuir no máximo 12 caracteres")
    private String fixedNumber;

    @NotBlankOrNull(message = "O subnúmero imobilizado não deve ser informado em branco")
    @Size(max = 4,message = "O subnúmero imobilizado deve possuir no máximo 4 caracteres")
    private String fixedSubnumber;

    @NotBlankOrNull(message = "O item reserva não deve ser informado em branco")
    @Size(max = 4,message = "O item reserva deve possuir no máximo 4 caracteres")
    private String reservationItem;

    @NotBlankOrNull(message = "O número reserva não deve ser informado em branco")
    @Size(max = 9,message = "O número reserva deve possuir no máximo 9 caracteres")
    private String reservationNumber;

    @NotNull(message = "O responsável não deve ser nulo")
    private String responsible;

    private String accountantCompany;

    private String activeClass;

    private String reason;

    private String reasonLabel;

    @NotNull(message = "O código de unidade não deve ser nulo")
    private String unityCode;

    private String mnemonic;

    private String unityCodeDescription;

    private String originUf;

    private Integer condition;
}
