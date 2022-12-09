package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.MessageResponseDTO;
import br.com.oi.sgis.dto.PaginateResponseDTO;
import br.com.oi.sgis.dto.StationDTO;
import br.com.oi.sgis.entity.Station;
import br.com.oi.sgis.exception.StationNotFoundException;
import br.com.oi.sgis.mapper.StationMapper;
import br.com.oi.sgis.repository.StationRepository;
import br.com.oi.sgis.repository.UfRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import br.com.oi.sgis.util.Utils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class StationService {

    private final StationRepository stationRepository;
    private static final StationMapper stationMapper = StationMapper.INSTANCE;
    private final ReportService reportService;
    private final AddressService addressService;
    private final UfRepository ufRepository;

    public List<StationDTO> listAll(){
        List<Station> allStations = stationRepository.findAll();
        return allStations.stream().map(stationMapper::toDTO).collect(Collectors.toList());
    }

    public PaginateResponseDTO<StationDTO> listPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term){
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if(term.isBlank())
            return PageableUtil.paginate(stationRepository.findAll(paging).map(stationMapper::toDTO));

        BigDecimal number = Utils.getNumberFromString(term);
        return  PageableUtil.paginate( stationRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), number, paging).map(stationMapper::toDTO));
    }

    @SneakyThrows
    public StationDTO findById(String id)  throws StationNotFoundException {
        Station station = verifyIfExists(id);
        return stationMapper.toDTO(station);
    }

    private Station verifyIfExists(String id) throws StationNotFoundException {
        return stationRepository.findById(id)
                .orElseThrow(()-> new StationNotFoundException(MessageUtils.STATION_NOT_FOUND_BY_ID.getDescription() + id));
    }

    @SneakyThrows
    private void validateStation(StationDTO dto){
        if(dto.getAddress() != null)
            addressService.findById(dto.getAddress().getId());
        if(dto.getUf() != null)
            ufRepository.findById(dto.getUf().getId())
                    .orElseThrow(()-> new ClassNotFoundException(MessageUtils.UF_NOT_FOUND_BY_ID.getDescription()));

    }

    public MessageResponseDTO createStation(StationDTO stationDTO) {
        Optional<Station> existStation = stationRepository.findById(stationDTO.getId());
        if(existStation.isPresent())
            throw new IllegalArgumentException(MessageUtils.STATION_ALREADY_REGISTERED.getDescription());
        validateStation(stationDTO);
        try {
            Station station = stationMapper.toModel(stationDTO);
            stationRepository.save(station);
            return createMessageResponse(station.getId(), MessageUtils.STATION_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.STATION_SAVE_ERROR.getDescription());
        }
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).status(status).build();
    }

    public MessageResponseDTO updateStation(StationDTO stationDTO) throws StationNotFoundException {
        verifyIfExists(stationDTO.getId());
        validateStation(stationDTO);
        try {
            Station station = stationMapper.toModel(stationDTO);
            stationRepository.save(station);
            return createMessageResponse(station.getId(), MessageUtils.STATION_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.STATION_UPDATE_ERROR.getDescription());
        }
    }

    public byte[] stationReport(String term, List<String> sortAsc, List<String> sortDesc) throws JRException, IOException {
        List<StationDTO> stationDTOS = listPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term).getData();
        if(stationDTOS== null || stationDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
        Map<String, Object> parameters = new HashMap<>();
        return reportService.fillStationReport(stationDTOS, parameters);
    }

    public void deleteById(String id) throws StationNotFoundException {
        verifyIfExists(id);
        try{
            stationRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.STATION_DELETE_ERROR.getDescription());
        }
    }
}
