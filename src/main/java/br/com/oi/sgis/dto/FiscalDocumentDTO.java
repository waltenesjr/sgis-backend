package br.com.oi.sgis.dto;

import br.com.oi.sgis.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FiscalDocumentDTO {

    @NotNull(message = "Você deve informar um número")
    private Long docNumber;
    @NotNull(message = "Você deve informar uma data")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime docDate;
    @NotBlank(message = "Você deve informar uma empresa")
    private String companyId;
    private String companyName;
    private CurrencyTypeDTO currencyType;
    @NotNull(message = "Você deve informar um valor")
    private BigDecimal value;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime activationDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date;
    @NotBlank(message = "Você deve informar uma marcação contábil")
    private String accountingCompany;
    private ContractDTO contract;

    public LocalDateTime getDate() {
        return Utils.onlyDate(date);
    }

    public LocalDateTime getDocDate() {
        return Utils.onlyDate(docDate);
    }

    public LocalDateTime getActivationDate() {
        return Utils.onlyDate(activationDate);
    }

}
