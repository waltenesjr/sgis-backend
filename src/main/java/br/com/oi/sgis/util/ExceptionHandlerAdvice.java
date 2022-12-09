package br.com.oi.sgis.util;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.exception.NotReprocessableUnityException;
import br.com.oi.sgis.exception.UnityException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLException;

@ControllerAdvice
public class ExceptionHandlerAdvice {


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponseDTO> handleException(Exception e) {
        String message = e.getMessage();
        if(e.getMessage().contains(";"))
            message = message.substring(0, message.indexOf(";"));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage("Erro!", message, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NotReprocessableUnityException.class)
    public ResponseEntity<MessageResponseDTO> handleInformationalSapReprocess(NotReprocessableUnityException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage(e.getMessage(), "Info!", HttpStatus.CONTINUE));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<MessageResponseDTO> handleException(SQLException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorMessage("Erro!", "Erro ao realizar operação", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<MessageResponseDTO> handleException(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                errorMessage("Erro",
                       "Erro ao realizar ação no banco de dados!", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponseDTO> handleException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage("Erro de validação!",
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler({InvalidFormatException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<MessageResponseDTO> handleException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UnityException.class)
    public ResponseEntity<MessageResponseDTO> handleException(UnityException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage(e,MessageUtils.UNITY_SAVED_ERROR.getDescription()));
    }

    private MessageResponseDTO errorMessage (Exception e, String title){
        return MessageResponseDTO.builder().title(title)
                .message(e.getLocalizedMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    private MessageResponseDTO errorMessage(String title, String message, HttpStatus status) {
        return MessageResponseDTO.builder().title(title)
                .message(message)
                .status(status).build();
    }

    private MessageResponseDTO errorMessage() {
        return MessageResponseDTO.builder().title("Erro de validação!")
                .message("Erro ao ler informação")
                .status(HttpStatus.BAD_REQUEST).build();
    }

}
