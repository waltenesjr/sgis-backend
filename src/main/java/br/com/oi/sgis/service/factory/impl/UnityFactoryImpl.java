package br.com.oi.sgis.service.factory.impl;

import br.com.oi.sgis.dto.AreaEquipamentDTO;
import br.com.oi.sgis.dto.TechnicalStaffDTO;
import br.com.oi.sgis.dto.UnityDTO;
import br.com.oi.sgis.entity.Situation;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.enums.ActiveClassEnum;
import br.com.oi.sgis.enums.RegisterReasonEnum;
import br.com.oi.sgis.enums.SituationEnum;
import br.com.oi.sgis.enums.TechiniqueCodeEnum;
import br.com.oi.sgis.exception.UnityException;
import br.com.oi.sgis.mapper.UnityMapper;
import br.com.oi.sgis.service.AreaEquipamentService;
import br.com.oi.sgis.service.TechnicalStaffService;
import br.com.oi.sgis.service.factory.UnityFactory;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UnityFactoryImpl implements UnityFactory {

    private final AreaEquipamentService areaEquipamentService;
    private final TechnicalStaffService technicalStaffService;
    private final Validator<Unity> validator;
    private static final UnityMapper unityMapper = UnityMapper.INSTANCE;


    private Unity unity;

    @SneakyThrows
    @Override
    public Unity makeNewSpareUnity(UnityDTO unityDTO) {

        unity = unityMapper.toModel(unityDTO);

        unity.setOriginUf(unity.getResponsible().getId().substring(0,2));
        unity.setRegisterReason(RegisterReasonEnum.CUS.getReason());
        addGeneralRules();
        addNewSpareUnitySituation();

        validator.validate(unity);

        return unity;
    }

    @SneakyThrows
    @Override
    public Unity makeRemovedFromSite(UnityDTO unityDTO) {
        unity = unityMapper.toModel(unityDTO);
        
        unity.setRegisterReason(RegisterReasonEnum.CRP.getReason());
        addGeneralRules();
        
        validator.validate(unity);
        
        return unity;
    }

    private void addGeneralRules() throws UnityException {
        additionalInformations();
        addActiveClass();
        addDefaultValues();
    }

    private void addDefaultValues() {
        if(unity.getProvider()==null)
            unity.setProvider(0);
        if(unity.getProviderAccountant() == null)
            unity.setProviderAccountant(0);
    }

    private void addActiveClass() throws UnityException {
        try{
            AreaEquipamentDTO areaEquipament = areaEquipamentService.findById(unity.getUnityCode().getId());
            if(areaEquipament.getTechniqueCode()== null)
                return;
            String activeClass = verifyActiveClass(areaEquipament);
            unity.setActiveClass(activeClass);

        }catch (Exception e){
            throw new UnityException(MessageUtils.AREA_EQUIPAMENT_NOT_FOUND_BY_ID.getDescription());
        }
    }

    private String verifyActiveClass(AreaEquipamentDTO areaEquipament) {

        if(Objects.equals(areaEquipament.getTechniqueCode(), TechiniqueCodeEnum.INST.getCod())){
            return ActiveClassEnum.ZXFERINS.getCod();
        }
        return  ActiveClassEnum.ZETSOBRE.getCod();
    }

    @SneakyThrows
    private void addNewSpareUnitySituation(){

        Situation unitySituation = verifyUnitySituation();
        unity.setSituationCode(unitySituation);

    }

    @SneakyThrows
    private Situation verifyUnitySituation(){
        TechnicalStaffDTO technicalStaff = technicalStaffService.findById(unity.getTechnician().getId());

        if(unity.getResponsible().getId().equals(technicalStaff.getDepartmentCode().getId())){
            return Situation.builder().id(SituationEnum.DIS.getCod()).build();
        }
        return  Situation.builder().id(SituationEnum.TRN.getCod()).build();
    }

    private void additionalInformations() {
        unity.setSituationDateChange(LocalDateTime.now());
        unity.setRegisterDate(LocalDateTime.now());
    }
}
