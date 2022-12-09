package br.com.oi.sgis.service.factory.impl;

import br.com.oi.sgis.dto.UnitExtractionReportDTO;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.service.factory.ExtractUnityFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
@RequiredArgsConstructor
public class ExtractUnityFactoryImpl implements ExtractUnityFactory {
    @Override
    public UnitExtractionReportDTO createExtractUnity(Unity unity) {
        return buildUnitExtractionReportDTO(unity);
    }

    private UnitExtractionReportDTO buildUnitExtractionReportDTO(Unity u){
        return UnitExtractionReportDTO.builder()
                .application(u.getUnityCode().getEquipModelCode().getEquipamentType().getApplication().getId())
                .applicationDesc(u.getUnityCode().getEquipModelCode().getEquipamentType().getApplication().getDescription())
                .barcode(u.getId()).box(u.getBox() != null ? u.getBox().getId() : "").composition(u.getBarcodeParent()).registerDate(u.getRegisterDate()).warrantyDate(u.getWarrantyDate())
                .situationDate(u.getSituationDateChange()).depositary(u.getDeposit().getId()).depositaryDesc(u.getDeposit().getDescription())
                .destiny(u.getDestination() != null ? u.getDestination().getId(): "").discontinuedFlag(u.getUnityCode().getDiscontinuedFlag())
                .station(u.getStation() != null ? u.getStation().getId(): "")
                .manufacturerEquip(u.getUnityCode().getEquipModelCode().getCompany().getId()).manufacturerEquipDesc(u.getUnityCode().getEquipModelCode().getCompany().getTradeName())
                .manufacturerUnit(u.getUnityCode().getCompany().getId()).manufacturerUnitDesc(u.getUnityCode().getCompany().getTradeName())
                .installationGroup(u.getCentral() !=null ? u.getCentral().getId() : "").installationRack(u.getInstallationRack())
                .installationClient(u.getClient() !=null ? u.getClient().getId(): "").installationClientDesc(u.getClient() !=null ? u.getClient().getTradeName() : "")
                .stationInst(u.getStationInst()!= null ? u.getStationInst().getId():"").location(u.getLocation()).mnemonic(u.getUnityCode().getMnemonic())
                .equipmentModel(u.getUnityCode().getEquipModelCode().getId()).equipmentModelDesc(u.getUnityCode().getEquipModelCode().getDescription())
                .unityCode(u.getUnityCode().getId()).unityDescription(u.getUnityCode().getDescription())
                .provider(u.getProvider()).serieNumber(u.getSerieNumber()).baNumber(u.getBaNumber()).observation(u.getObservation())
                .providerResponsible(u.getProviderResponsible() != null ? u.getProviderResponsible().getId():"")
                .providerResponsibleDesc(u.getProviderResponsible() != null ? u.getProviderResponsible().getTradeName():"")
                .responsible(u.getResponsible().getId()).responsibleDescription(u.getResponsible().getDescription())
                .situation(u.getSituationCode().getId()).situationDescription(u.getSituationCode().getDescription())
                .technic(u.getUnityCode().getEquipModelCode().getEquipamentType().getTechnique().getId())
                .technician(u.getTechnician().getId()).technicianName(u.getTechnician().getTechnicianName()).installationTechnician(u.getInstallationTechnician() != null ? u.getInstallationTechnician().getId():"")
                .installationTechnicianDesc(u.getInstallationTechnician() != null ? u.getInstallationTechnician().getTechnicianName():"")
                .technology(u.getUnityCode().getEquipModelCode().getTechnology()!= null ? u.getUnityCode().getEquipModelCode().getTechnology().getId(): "")
                .boxType(u.getBox() != null ? u.getBox().getBoxType().getId() :"").boxTypeDesc(u.getBox() != null ? u.getBox().getBoxType().getDescription() :"")
                .equipmentType(u.getUnityCode().getEquipModelCode().getEquipamentType().getId()).equipmentTypeDesc(u.getUnityCode().getEquipModelCode().getEquipamentType().getEquipamentName())
                .discontinuedFlagEquip(u.getUnityCode().getEquipModelCode().getDescountFlag()).providerAccountant(u.getProviderAccountant())
                .uf(u.getResponsible().getId().substring(0,2)).value(u.getValue() != null ? u.getValue() : BigDecimal.ZERO)
                .areaValue(u.getUnityCode().getValue() != null ? u.getUnityCode().getValue(): BigDecimal.ZERO ).quantity(1)
                .build();
    }
}
