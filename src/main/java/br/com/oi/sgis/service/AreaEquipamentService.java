package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.AreaEquipament;
import br.com.oi.sgis.entity.ElectricalPropEquip;
import br.com.oi.sgis.enums.ActiveClassEnum;
import br.com.oi.sgis.enums.TechiniqueCodeEnum;
import br.com.oi.sgis.exception.AreaEquipamentNotFoundException;
import br.com.oi.sgis.exception.UnityException;
import br.com.oi.sgis.mapper.AreaEquipamentMapper;
import br.com.oi.sgis.repository.AreaEquipamentRepository;
import br.com.oi.sgis.repository.ElectricalPropEquipRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AreaEquipamentService {

    private final AreaEquipamentRepository areaEquipamentRepository;
    private static final AreaEquipamentMapper areaEquipamentMapper = AreaEquipamentMapper.INSTANCE;
    private final ReportService reportService;
    private final TechnicalStaffService technicalStaffService;
    private final ModelEquipTypeService modelEquipTypeService;
    private final ElectricalPropEquipRepository electricalPropEquipRepository;

    @Transactional
    public MessageResponseDTO createAreaEquipament(AreaEquipamentDTO areaEquipamentDTO){
        Optional<AreaEquipament> existAreaEquipament = areaEquipamentRepository.findById(areaEquipamentDTO.getId());
        if(existAreaEquipament.isPresent())
            throw new IllegalArgumentException(MessageUtils.ALREADY_EXISTS.getDescription());
        validate(areaEquipamentDTO);
        try {
            AreaEquipament areaEquipament = areaEquipamentMapper.toModel(areaEquipamentDTO);
            areaEquipament.setDate(LocalDateTime.now());
            areaEquipamentRepository.save(areaEquipament);
            saveElectricalProps(areaEquipament.getElectricalProperties());
            return createMessageResponse(areaEquipament.getId(), MessageUtils.AREA_EQUIPAMENT_SAVE_SUCCESS.getDescription(), HttpStatus.CREATED);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.AREA_EQUIPAMENT_SAVE_ERROR.getDescription());
        }
    }

    public MessageResponseDTO updateAreaEquipament(AreaEquipamentDTO areaEquipamentDTO) throws AreaEquipamentNotFoundException {
        verifyIfExists(areaEquipamentDTO.getId());
        validate(areaEquipamentDTO);
        try {
            AreaEquipament areaEquipament = areaEquipamentMapper.toModel(areaEquipamentDTO);
            updateElectricalProps(areaEquipament);
            areaEquipamentRepository.save(areaEquipament);
            return createMessageResponse(areaEquipament.getId(), MessageUtils.AREA_EQUIPAMENT_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.AREA_EQUIPAMENT_UPDATE_ERROR.getDescription());
        }
    }

    @SneakyThrows
    private void validate(AreaEquipamentDTO areaEquipamentDTO){
        modelEquipTypeService.findById(areaEquipamentDTO.getEquipModelCode().getId());
        if(areaEquipamentDTO.getTechnician() != null)
            technicalStaffService.findById(areaEquipamentDTO.getTechnician().getId());
    }

    public PaginateResponseDTO<AreaEquipamentDTO> searchByTermsPaginated(Integer pageNo, Integer pageSize,List<String> sortAsc, List<String> sortDesc, String term, LocalDateTime date) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        BigDecimal number = Utils.getNumberFromString(term);

        if(term.isBlank() && date == null)
            return PageableUtil.paginate(areaEquipamentRepository.findAllByDiscontinuedFlagFalse(paging).map(areaEquipamentMapper::toDTO));

        if(date != null)
            return PageableUtil.paginate( areaEquipamentRepository.findLikeWithDate(term.toUpperCase(Locale.ROOT).trim().replaceAll("\\s+", " "), number, date,paging).map(areaEquipamentMapper::toDTO));

        return PageableUtil.paginate( areaEquipamentRepository.findLike(term.toUpperCase(Locale.ROOT).trim().replaceAll("\\s+", " "), number, paging).map(areaEquipamentMapper::toDTO));
    }

    @SneakyThrows
    public AreaEquipamentDTO findById(String id) throws AreaEquipamentNotFoundException {
        AreaEquipament areaEquipament = verifyIfExists(id);
        return areaEquipamentMapper.toDTO(areaEquipament);
    }

    public void deleteById(String id) throws AreaEquipamentNotFoundException {
        verifyIfExists(id);
        try {
            areaEquipamentRepository.deleteById(id);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.AREA_EQUIPAMENT_DELETE_ERROR.getDescription());
        }
    }

    private AreaEquipament verifyIfExists(String id) throws AreaEquipamentNotFoundException {
        return areaEquipamentRepository.findById(id)
                .orElseThrow(()-> new AreaEquipamentNotFoundException(MessageUtils.AREA_EQUIPAMENT_NOT_FOUND_BY_ID.getDescription() + id));
    }

    private MessageResponseDTO createMessageResponse(String id, String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message + id).title("Sucesso!").status(status).build();
    }


    public ActiveClassEnum getActiveClass(String idUnityCode) throws AreaEquipamentNotFoundException {
        AreaEquipamentDTO areaEquipament = findById(idUnityCode);
        if(areaEquipament.getTechniqueCode()== null)
            return null;

        if(Objects.equals(areaEquipament.getTechniqueCode(), TechiniqueCodeEnum.INST.getCod())){
            return ActiveClassEnum.ZXFERINS;
        }
        return  ActiveClassEnum.ZETSOBRE;
    }

    public PaginateResponseDTO<Object> searchMnemonics(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
        if(term.isBlank())
            return PageableUtil.paginate(areaEquipamentRepository.findAllByDistinctMnemonic(paging).map(m-> new MnemonicDTO((String) m)));
        return PageableUtil.paginate(areaEquipamentRepository.findAllByDistinctMnemonic(term,paging).map(m-> new MnemonicDTO((String) m)));

    }

    public AreaEquipament findByMnemonic(String mnemonic) throws AreaEquipamentNotFoundException {
        return areaEquipamentRepository.findTopByMnemonicContains(mnemonic)
                .orElseThrow(()-> new AreaEquipamentNotFoundException(MessageUtils.AREA_EQUIPAMENT_NOT_FOUND_BY_MNEMONIC.getDescription() + mnemonic));

    }

    public byte[] areaEquipamentReport(String term, List<String> sortAsc, List<String> sortDesc, LocalDateTime date) throws JRException, IOException {
        List<AreaEquipamentDTO> areaEquipamentDTOS = searchByTermsPaginated(0, Integer.MAX_VALUE, sortAsc, sortDesc, term, date).getData();
        if(areaEquipamentDTOS== null || areaEquipamentDTOS.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nameReport", "Relatório de Modelos de Unidades");
        parameters.put("column1", "CÓD. PLACA");
        parameters.put("column2", "DESCRIÇÃO");
        parameters.put("column3", "CÓD. MODELO");

        List<GenericReportDTO> genericReport = areaEquipamentDTOS.stream().map(r ->
                GenericReportDTO.builder().data1(r.getId()).data2(r.getDescription()).data3(r.getEquipModelCode().getId()).build()
        ).collect(Collectors.toList());

        return reportService.genericReportThreeColumns(genericReport, parameters);

    }


    private void saveElectricalProps(List<ElectricalPropEquip> electricalProperties) throws UnityException {
        try{
            if((electricalProperties != null) && !electricalProperties.isEmpty())
                electricalPropEquipRepository.saveAll(electricalProperties);
        }catch (RuntimeException e){
            throw new UnityException(MessageUtils.EQUIP_PROP_ELETRIC_ERROR.getDescription());
        }
    }

    private void updateElectricalProps(AreaEquipament equipament) throws AreaEquipamentNotFoundException, UnityException {
        List<ElectricalPropEquip> electricalProperties = equipament.getElectricalProperties();
        List<ElectricalPropEquip> oldElectricalProps = verifyIfExists(equipament.getId()).getElectricalProperties();
        if(electricalProperties.isEmpty() && oldElectricalProps.isEmpty())
            return;
        if(oldElectricalProps.isEmpty() && !electricalProperties.isEmpty()) {
            saveElectricalProps(electricalProperties);
        }
        else if(electricalProperties.isEmpty() && !oldElectricalProps.isEmpty()){
            electricalPropEquipRepository.deleteAll(oldElectricalProps);
        }else {
            electricalPropEquipRepository.deleteAll(oldElectricalProps);
            saveElectricalProps(electricalProperties);
        }

    }
}
