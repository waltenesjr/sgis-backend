package br.com.oi.sgis.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder @Data
public class GenericQueryResultDTO {
    List< List<String>> result;
    List<String> columns;
}
