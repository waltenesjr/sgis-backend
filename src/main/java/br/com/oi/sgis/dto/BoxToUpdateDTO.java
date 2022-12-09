package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoxToUpdateDTO {
    private BoxDTO box;
    private List<UnityDTO> unities;
    private List<UnityDTO> unitiesToRemove;
}
