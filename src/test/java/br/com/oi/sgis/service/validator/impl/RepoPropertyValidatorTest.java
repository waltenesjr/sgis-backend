package br.com.oi.sgis.service.validator.impl;

import br.com.oi.sgis.dto.RepoPropertyDTO;
import br.com.oi.sgis.dto.StationDTO;
import br.com.oi.sgis.exception.StationNotFoundException;
import br.com.oi.sgis.service.StationService;
import lombok.SneakyThrows;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class RepoPropertyValidatorTest {
    @InjectMocks
    private RepoPropertyValidator repoPropertyValidator;

    @Mock
    private StationService stationService;

    @Test @SneakyThrows
    void shouldValidate(){
        RepoPropertyDTO repoPropertyDTO = new EasyRandom().nextObject(RepoPropertyDTO.class);
        StationDTO stationDTO = new EasyRandom().nextObject(StationDTO.class);

        Mockito.doReturn(stationDTO).when(stationService).findById(Mockito.any());

        Assertions.assertDoesNotThrow(()->repoPropertyValidator.validate(repoPropertyDTO));
    }

    @Test @SneakyThrows
    void shouldThrowExceptionStationNotFound(){
        RepoPropertyDTO repoPropertyDTO = new EasyRandom().nextObject(RepoPropertyDTO.class);
        Mockito.doThrow(StationNotFoundException.class).when(stationService).findById(Mockito.any());

        Assertions.assertThrows(IllegalArgumentException.class, ()->repoPropertyValidator.validate(repoPropertyDTO));
    }
}