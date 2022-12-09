package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class MessageResponseDTO {
    private String message;
    private String title;
    private HttpStatus status;
}
