package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder @AllArgsConstructor
public class MnemonicDTO {
    private String mnemonic;
}
