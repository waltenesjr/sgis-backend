package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.CompositionUnityDTO;
import br.com.oi.sgis.dto.CompositionUnityReportDTO;
import br.com.oi.sgis.dto.UnityBarcodeDTO;
import br.com.oi.sgis.dto.UnityDTO;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.exception.UnityNotFoundException;
import br.com.oi.sgis.mapper.UnityMapper;
import br.com.oi.sgis.repository.UnityRepository;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.Utils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CompositionService {
    private final UnityRepository unityRepository;
    private static final UnityMapper unityMapper = UnityMapper.INSTANCE;
    private final ReportService reportService;

    private Unity verifyIfExists(String id) throws UnityNotFoundException {
        return unityRepository.findById(id)
                .orElseThrow(()-> new UnityNotFoundException(MessageUtils.UNITY_NOT_FOUND_BY_ID.getDescription() + id));
    }
    @SneakyThrows
    public CompositionUnityDTO getComposition(String id){
        Unity unity = verifyIfExists(id);
        verifyValidForComposition(unity);
        verifyValidUnityDepartment(unity);
        return getCompositionUnityDTO(id);
    }

    private CompositionUnityDTO getCompositionUnityDTO(String id) {
        List<UnityDTO> unities = unityRepository.findUnitiesComposition(id).stream().map(unityMapper::toDTO).collect(Collectors.toList());
        return CompositionUnityDTO.builder().unityModel(id).unitiesItem(unities).unitiesItemToRemove(List.of()).build();
    }

    private void verifyValidUnityDepartment(Unity unity) {
        if(!unity.getDeposit().getId().equals(Utils.getUser().getDepartmentCode().getId()))
            throw new IllegalArgumentException(MessageUtils.ITEM_DIFFERENT_DEP.getDescription());
    }

    private void verifyValidForComposition(Unity unity)  {
        if(unity.getBarcodeParent()!=null && !unity.getBarcodeParent().isEmpty())
            throw new IllegalArgumentException(MessageUtils.ITEM_NOT_MODEL.getDescription());
    }

    @SneakyThrows
    public CompositionUnityDTO addComposition(String modelId, String itemId) {
        Unity model = verifyIfExists(modelId);
        Unity item = verifyIfExists(itemId);
        verifyValidForComposition(item);
        verifyValidUnityDepartment(item);
        if(modelId.equals(itemId))
            throw new IllegalArgumentException(MessageUtils.ITEM_INVALID_BARCODE.getDescription());

        try {
            unityRepository.updateComposition(modelId, itemId,model.getResponsible().getId());
        }catch (RuntimeException e){
            throw new IllegalArgumentException(MessageUtils.UNITY_COMPOSITION_ERROR.getDescription());
        }
        return getCompositionUnityDTO(modelId);
    }

    public CompositionUnityDTO removeComposition(CompositionUnityDTO compositionUnityDTO) {
        List<UnityDTO> itensToRemove = compositionUnityDTO.getUnitiesItemToRemove();
        if(itensToRemove.isEmpty())
            throw new IllegalArgumentException(MessageUtils.UNITY_REMOVE_NO_ITENS_ERROR.getDescription());

        String responsible = Utils.getUser().getDepartmentCode().getId();
        try {
            itensToRemove.forEach(i -> unityRepository.updateComposition(null, i.getId(),responsible ));

        }catch (RuntimeException e){
            throw new IllegalArgumentException(MessageUtils.UNITY_REMOVE_COMPOSITION_ERROR.getDescription());
        }
        return getCompositionUnityDTO(compositionUnityDTO.getUnityModel());

    }

    @SneakyThrows
    public byte[] report(CompositionUnityDTO compositionUnityDTO) {
        UnityBarcodeDTO unityModel = getUnityBarcodeDTO(compositionUnityDTO.getUnityModel());
        List<UnityBarcodeDTO> unitiesItem = getUnitiesItem(compositionUnityDTO.getUnitiesItem());
        CompositionUnityReportDTO reportDTO = CompositionUnityReportDTO.builder()
                .unityModel(unityModel).unitiesItem(unitiesItem).build();
        return reportService.report(reportDTO);
    }

    private List<UnityBarcodeDTO> getUnitiesItem(List<UnityDTO> unitiesItem) {
        return unitiesItem.stream().map(ui -> {
            try {
                return getUnityBarcodeDTO(ui.getId());
            } catch (UnityNotFoundException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    private UnityBarcodeDTO getUnityBarcodeDTO(String unityId) throws UnityNotFoundException {
        Unity unity = verifyIfExists(unityId);
         String description = unity.getUnityCode().getId() + " - " + unity.getUnityCode().getEquipModelCode().getId();
        UnityBarcodeDTO unityModel = new UnityBarcodeDTO();
        unityModel.setId(unity.getId());
        unityModel.setDescription(description);
        unityModel.setSituation(unity.getSituationCode().getId() + " - " + unity.getSituationCode().getDescription());
        unityModel.setBarcodeImage(unity.getId());
        return unityModel;
    }
}
