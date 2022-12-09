package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.RepoPropertyDTO;
import br.com.oi.sgis.exception.StationNotFoundException;
import br.com.oi.sgis.service.StationService;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepoPropertyValidator implements Validator<RepoPropertyDTO> {

    private final StationService stationService;

    @Override
    public void validate(RepoPropertyDTO repoPropertyDTO) {
        validateStation(repoPropertyDTO.getIdStation());
    }

    private void validateStation(String stationId) {
        try {
            stationService.findById(stationId);
        }catch (StationNotFoundException e){
            throw new IllegalArgumentException(MessageUtils.STATION_INVALID.getDescription());
        }
    }

}
