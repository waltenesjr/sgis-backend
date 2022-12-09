package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.Box;
import br.com.oi.sgis.exception.BoxNotFoundException;
import br.com.oi.sgis.mapper.BoxMapper;
import br.com.oi.sgis.mapper.BoxTypeMapper;
import br.com.oi.sgis.mapper.UnityMapper;
import br.com.oi.sgis.repository.BoxRepository;
import br.com.oi.sgis.repository.BoxTypeRepository;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.util.LabelUtils;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BoxService {

    private final BoxRepository boxRepository;
    private final BoxTypeRepository boxTypeRepository;
    private final UnityRepository unityRepository;
    private static final BoxMapper boxMapper = BoxMapper.INSTANCE;
    private final ReportService reportService;


    public PaginateResponseDTO<Object> listPaginated(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term){
        pageNo = PageableUtil.correctPageNo(pageNo);
        Map<String, String> sortMap = BoxMapper.getMappedValues();
        Pageable paging = PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc,sortMap ));
        if(term.isBlank())
            return PageableUtil.paginate(boxRepository.findAll(paging).map(boxMapper::toDTO),sortMap);
        return PageableUtil.paginate(boxRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(boxMapper::toDTO),sortMap);
    }

    @SneakyThrows
    public BoxDTO findById(String id){
        Box box = verifyIfExists(id);
        return boxMapper.toDTO(box);
    }


    @SneakyThrows
    public void deleteById(String id) {
        verifyIfExists(id);
        boxRepository.deleteById(id);
    }

    private Box verifyIfExists(String id) throws BoxNotFoundException {
        return boxRepository.findById(id)
                .orElseThrow(()-> new BoxNotFoundException(MessageUtils.BOX_NOT_FOUND_BY_ID.getDescription() + id));
    }

    public List<BoxTypeDTO> listBoxType(){
        BoxTypeMapper mapper = BoxTypeMapper.INSTANCE;
        return boxTypeRepository.findAll().parallelStream().map(mapper::toDTO).collect(Collectors.toList());
    }

    public BoxToUpdateDTO getBoxToUpdate(String id) throws BoxNotFoundException {
        Box box = verifyIfExists(id);
        List<UnityDTO> unities = boxUnities(box.getId());
        return BoxToUpdateDTO.builder().box(boxMapper.toDTO(box)).unities(unities).unitiesToRemove(List.of()).build();
    }

    private List<UnityDTO> boxUnities(String boxId) {
        UnityMapper mapper = UnityMapper.INSTANCE;
        return unityRepository.findUnityByBox(boxId).parallelStream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = IllegalArgumentException.class)
    public MessageResponseDTO updateBox(BoxToUpdateDTO boxToUpdateDTO) throws BoxNotFoundException {
        try {
            verifyIfExists(boxToUpdateDTO.getBox().getId());
            removeUnitiesFromBox(boxToUpdateDTO.getUnitiesToRemove());
            boxRepository.updateBox(boxMapper.toModel(boxToUpdateDTO.getBox()));
        }catch (RuntimeException e){
            throw new IllegalArgumentException(MessageUtils.BOX_UPDATE_ERROR.getDescription());
        }
        return createMessageResponse(MessageUtils.BOX_UPDATE_SUCCESS.getDescription(), HttpStatus.OK);

    }

    private void removeUnitiesFromBox(List<UnityDTO> unitiesToRemove) {
        if(unitiesToRemove!= null && !unitiesToRemove.isEmpty())
            unityRepository.removeFromBox(unitiesToRemove.parallelStream().map(UnityDTO::getId).collect(Collectors.toList()));
    }

    private MessageResponseDTO createMessageResponse(String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message).title("Sucesso!").status(status).build();
    }

    @SneakyThrows
    public byte[] summaryBoxReport(String boxId) {
        Box box = verifyIfExists(boxId);
        List<UnityDTO> unities = boxUnities(boxId);
        List<UnityBarcodeDTO> unitiesBarcode = new ArrayList<>();
        if(unities.isEmpty())
            throw new IllegalArgumentException(MessageUtils.EMPTY_BOX.getDescription());
        unities.forEach(u -> {
            UnityBarcodeDTO unityModel = new UnityBarcodeDTO();
            unityModel.setId(u.getId());
            unityModel.setDescription(u.getUnityCode().getId() + " - " + u.getUnityCode().getEquipModelCode().getId());
            unityModel.setSituation(u.getSituationCode().getId() + " - " + u.getSituationCode().getDescription());
            unitiesBarcode.add(unityModel);
        });

        try {
            Map<String, Object> parameters = new HashMap<>();
            BufferedImage barcodeBox = LabelUtils.getBarcodeImage(boxId);
            BigDecimal weight = unities.parallelStream().map(u -> u.getUnityCode().getWeight()).reduce(BigDecimal::add)
                    .orElse(BigDecimal.ZERO);

            String description = box.getDescription() + "\n" +
                    box.getBoxType().getId() + " - " + box.getBoxType().getDescription();
            parameters.put("barcodeBox", barcodeBox);
            parameters.put("boxId", box.getId());
            parameters.put("descricao", description);
            parameters.put("peso", weight.toString());
            return reportService.summaryBoxReport(unitiesBarcode, parameters);
        }catch (Exception e ){
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }
}
