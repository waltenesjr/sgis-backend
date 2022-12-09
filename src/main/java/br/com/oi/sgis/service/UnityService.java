package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.BarcodeFAS;
import br.com.oi.sgis.entity.ElectricalPropUnity;
import br.com.oi.sgis.entity.RepairTicket;
import br.com.oi.sgis.entity.Unity;
import br.com.oi.sgis.entity.view.WarrantyView;
import br.com.oi.sgis.enums.InstallationReasonEnum;
import br.com.oi.sgis.enums.SituationEnum;
import br.com.oi.sgis.exception.NotReprocessableUnityException;
import br.com.oi.sgis.exception.UnityException;
import br.com.oi.sgis.exception.UnityNotFoundException;
import br.com.oi.sgis.mapper.InstallationTransferMapper;
import br.com.oi.sgis.mapper.RepairTicketMapper;
import br.com.oi.sgis.mapper.UnityMapper;
import br.com.oi.sgis.mapper.WarrantyViewMapper;
import br.com.oi.sgis.repository.*;
import br.com.oi.sgis.service.factory.ExtractUnityFactory;
import br.com.oi.sgis.service.factory.UnityFactory;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.PageableUtil;
import br.com.oi.sgis.util.SortUtil;
import br.com.oi.sgis.util.Utils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UnityService {

    private final UnityRepository unityRepository;
    private final BarcodeFASRepository barcodeFASRepository;
    private final ElectricalPropUnityRepository electricalPropUnityRepository;
    private final UnityFactory unityFactory;
    private final NasphService nasphService;
    private final ReportService reportService;
    private final ReprocessUnityService reprocessUnityService;
    private final WarrantyViewRepository warrantyViewRepository;
    private final RepairTicketRepository repairTicketRepository;
    private final ExtractUnityFactory extractUnityFactory;
    private static final UnityMapper unityMapper = UnityMapper.INSTANCE;
    private static final InstallationTransferMapper installationTransferMapper = InstallationTransferMapper.INSTANCE;


    public List<UnityDTO> listAll(){
        List<Unity> allUnities = unityRepository.findAll();
        return  allUnities.stream().map(unityMapper::toDTO).collect(Collectors.toList());
    }

    public PaginateResponseDTO<Object> listPaginated(Integer pageNo, Integer pageSize, String sortBy, String order){
        pageNo = PageableUtil.correctPageNo(pageNo);

        Sort.Direction direction = Sort.Direction.fromString(order);
        Pageable paging = PageRequest.of(pageNo, pageSize,direction, sortBy);
        return PageableUtil.paginate(unityRepository.findAll(paging).map(unityMapper::toDTO));
    }

    @SneakyThrows
    public MessageResponseDTO createNewSpareUnity(UnityDTO unityDTO){
        Unity unityToSave = unityFactory.makeNewSpareUnity(unityDTO);
        Unity savedUnity = unityRepository.save(unityToSave);
        saveElectricalProps(unityToSave.getElectricalProperties());
        return createMessageResponse(MessageUtils.UNITY_SAVED_SUCCESS.getDescription() + savedUnity.getId(),
                HttpStatus.CREATED);
    }

    private void saveElectricalProps(List<ElectricalPropUnity> electricalProperties) throws UnityException {
        try{
            if((electricalProperties != null) && !electricalProperties.isEmpty())
                electricalPropUnityRepository.saveAll(electricalProperties);
        }catch (RuntimeException e){
            throw new UnityException(MessageUtils.UNITY_PROP_ELETRIC_ERROR.getDescription());
        }
    }

    @SneakyThrows
    public MessageResponseDTO createRemovedFromSiteUnity(UnityDTO unityDTO){
        Unity unityToSave = unityFactory.makeRemovedFromSite(unityDTO);
        Unity savedUnity = unityRepository.save(unityToSave);
        saveElectricalProps(unityToSave.getElectricalProperties());
        return createMessageResponse(MessageUtils.UNITY_SAVED_SUCCESS.getDescription() + savedUnity.getId(),
                HttpStatus.CREATED);
    }

    @SneakyThrows
    public MessageResponseDTO updateUnity(UnityDTO unityDTO){
        Unity unity = verifyIfExists(unityDTO.getId());
        verifySapError(unity);
        try {
            updateElectricalProps(unity);
            unityRepository.updateUnity(unityDTO);
        }catch (RuntimeException e){
            throw new UnityException(MessageUtils.UNITY_UPDATE_ERROR.getDescription());
        }
        return createMessageResponse(MessageUtils.UNITY_UPDATE_SUCCESS.getDescription(),
                HttpStatus.OK);
    }

    private void updateElectricalProps(Unity unity) throws UnityNotFoundException, UnityException {
        List<ElectricalPropUnity> electricalProperties = unity.getElectricalProperties();
        List<ElectricalPropUnity> oldElectricalProps = verifyIfExists(unity.getId()).getElectricalProperties();
        if(electricalProperties.isEmpty() && oldElectricalProps.isEmpty())
            return;
        if(oldElectricalProps.isEmpty() && !electricalProperties.isEmpty()) {
            saveElectricalProps(electricalProperties);
        }
        else if(electricalProperties.isEmpty() && !oldElectricalProps.isEmpty()){
            electricalPropUnityRepository.deleteAll(oldElectricalProps);
        }else {
            electricalPropUnityRepository.deleteAll(oldElectricalProps);
            saveElectricalProps(electricalProperties);
        }

    }

    private void verifySapError(Unity unity) throws UnityException {
        if(unity.getSapStatus()==null || !unity.getSapStatus().equals("0"))
            throw new UnityException(MessageUtils.UNITY_UPDATE_SAP_ERROR.getDescription());
    }

    @SneakyThrows
    public UnityDTO findById(String id)  {
        Unity unity = verifyIfExists(id);
        return unityMapper.toDTO(unity);
    }

    @SneakyThrows
    public InstallationTransferDTO getInstallationTransfer(String id)  {
        Unity unity = verifyIfExists(id);
        return installationTransferMapper.toDTO(unity);
    }

    private Unity verifyIfExists(String id) throws UnityNotFoundException {
        return unityRepository.findById(id)
                .orElseThrow(()-> new UnityNotFoundException(MessageUtils.UNITY_NOT_FOUND_BY_ID.getDescription() + id));
    }

    private MessageResponseDTO createMessageResponse(String message, HttpStatus status) {
        return MessageResponseDTO.builder().message(message).title("Sucesso!").status(status).build();
    }

    public PaginateResponseDTO<Object> searchByTermsPaginated(Integer pageNo, Integer pageSize,List<String> sortAsc, List<String> sortDesc, String term) {
        Pageable paging = getPageable(pageNo, pageSize, sortAsc, sortDesc);

        if(term.isBlank())
            return PageableUtil.paginate(unityRepository.findAll(paging).map(unityMapper::toDTO));

        return( PageableUtil.paginate(unityRepository.findLike(term.toUpperCase(Locale.ROOT).trim(), paging).map(unityMapper::toDTO)));
    }

    public PaginateResponseDTO<Object> listUnitiesForPropertyTransf(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        Pageable paging = getPageable(pageNo, pageSize, sortAsc, sortDesc);

        List<String> situationsUnity = List.of(SituationEnum.DIS.getCod(),SituationEnum.EMU.getCod(),
                SituationEnum.EMP.getCod(),SituationEnum.USO.getCod(),SituationEnum.DEF.getCod());

        if(term.isBlank())
            return PageableUtil.paginate(unityRepository.findAllBySituationCodeContains(situationsUnity, paging).map(unityMapper::toDTO));

        return( PageableUtil.paginate(unityRepository.findAllBySituationCodeContainsLike(term.toUpperCase(Locale.ROOT).trim(), situationsUnity, paging).map(unityMapper::toDTO)));
    }

    public MessageResponseDTO updatePropertyTransf(UnityDTO unityDTO) {
        findById(unityDTO.getId());

        return nasphService.transfProperty(unityDTO);
    }

    public PaginateResponseDTO<Object> listUnitiesForPropertyRepos(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        Pageable paging = getPageable(pageNo, pageSize, sortAsc, sortDesc);

        List<String> situationsUnity = List.of(SituationEnum.TRC.getCod(),SituationEnum.TRL.getCod(),
                SituationEnum.REP.getCod(),SituationEnum.TRR.getCod());

        if(term.isBlank())
            return PageableUtil.paginate(unityRepository.findAllForPropertyRepos(situationsUnity, Utils.getUser().getDepartmentCode().getId(), paging).map(unityMapper::toDTO));

        return( PageableUtil.paginate(unityRepository.findAllForPropertyReposContains(term.toUpperCase(Locale.ROOT).trim(), situationsUnity,Utils.getUser().getDepartmentCode().getId(), paging).map(unityMapper::toDTO)));
    }

    public MessageResponseDTO updatePropertyRepo(UnityDTO unityDTO) {
        findById(unityDTO.getId());

        return nasphService.repoProperty(unityDTO);
    }

    public PaginateResponseDTO<Object> listUnitiesForUpdateSituation(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        Pageable paging = getPageable(pageNo, pageSize, sortAsc, sortDesc);

        List<String> situationsUnity = List.of(SituationEnum.DIS.getCod(),SituationEnum.DEF.getCod(),
                SituationEnum.OFE.getCod(),SituationEnum.BXI.getCod(),SituationEnum.BXP.getCod(), SituationEnum.RES.getCod());

        return getUnitiesBySitAndDepartment(term, paging, situationsUnity);
    }

    private Pageable getPageable(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc) {
        pageNo = PageableUtil.correctPageNo(pageNo);
        return PageRequest.of(pageNo, pageSize, SortUtil.createSorts(sortAsc, sortDesc));
    }

    public MessageResponseDTO updateUnitySituation(UnitySituationDTO unitySituationDTO) {
        return nasphService.updateUnitySituation(unitySituationDTO);
    }

    public PaginateResponseDTO<Object> listUnitiesForUnitySwap(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        Pageable paging = getPageable(pageNo, pageSize, sortAsc, sortDesc);

        List<String> situationsUnity = List.of(SituationEnum.DIS.getCod(),SituationEnum.DEF.getCod());

        return getUnitiesBySitAndDepartment(term, paging, situationsUnity);
    }

    private PaginateResponseDTO<Object> getUnitiesBySitAndDepartment(String term, Pageable paging, List<String> situationsUnity) {
        if (term.isBlank())
            return PageableUtil.paginate(unityRepository.findAllBySituationCodeContainsAndDepartment(situationsUnity, Utils.getUser().getDepartmentCode().getId(), paging).map(unityMapper::toDTO));

        return (PageableUtil.paginate(unityRepository.findAllBySituationCodeContainsAndDepartment(term.toUpperCase(Locale.ROOT).trim(), situationsUnity, Utils.getUser().getDepartmentCode().getId(), paging).map(unityMapper::toDTO)));
    }

    public MessageResponseDTO updateUnitySwap(UnitySwapDTO unitySwapDTO) {
        return nasphService.updateUnitySwap(unitySwapDTO);
    }
    public PaginateResponseDTO<Object> listUnitiesToPlanInstallation(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        Pageable paging = getPageable(pageNo, pageSize, sortAsc, sortDesc);

        List<String> situationsUnity = List.of(SituationEnum.USO.getCod(),SituationEnum.DIS.getCod(), SituationEnum.PRE.getCod(), SituationEnum.EMU.getCod(), SituationEnum.EMP.getCod());

        return getUnitiesBySitAndDepartment(term, paging, situationsUnity);
    }

    public MessageResponseDTO updatePlanInstallation(PlanInstallationDTO planInstallationDTO) throws UnityNotFoundException {
        Unity unity = verifyIfExists(planInstallationDTO.getUnityId());
        if(unity.getCentral()!=null)
            planInstallationDTO.setCentral(unity.getCentral().getId());
        return nasphService.updatePlanInstallation(planInstallationDTO);
    }

    public MessageResponseDTO updateUnityWriteOff(UnityWriteOffDTO unityWriteOffDTO) {
        return nasphService.updateUnityWriteOff(unityWriteOffDTO);
    }

    public MessageResponseDTO registerBoNumber(RegisterBoDTO registerBoDTO) throws UnityNotFoundException {
        Unity unity = verifyIfExists(registerBoDTO.getUnityId());
        if(unity.getBoNumber()!=null)
            throw new IllegalArgumentException(String.format(MessageUtils.UNITY_BO_ALREADY_EXISTS_ERROR.getDescription(), unity.getBoNumber()));


        if(unity.getInstalationReason() == null ||!InstallationReasonEnum.steals().contains(InstallationReasonEnum.valueOf(unity.getInstalationReason()))) {
            throw new IllegalArgumentException(MessageUtils.UNITY_BO_REASON_ERROR.getDescription());
        }

        InstallationReasonEnum installationReason = InstallationReasonEnum.valueOf(unity.getInstalationReason());
        unity.setReasonForWriteOff(installationReason.getLostReason());
        unity.setBoNumber(registerBoDTO.getBoNumber());
        unityRepository.save(unity);

        return  createMessageResponse(MessageUtils.UNITY_BO_REGISTER_SUCCESS.getDescription(),
                HttpStatus.CREATED);
    }

    @SneakyThrows
    public MessageResponseDTO recoverItem(RecoverItemDTO recoverItemDTO) {
        Unity unity = verifyIfExists(recoverItemDTO.getUnityId());

        if(recoverItemDTO.getStationId() == null && unity.getStation()!=null)
            recoverItemDTO.setStationId(unity.getStation().getId());
        if((recoverItemDTO.getLocation() == null|| recoverItemDTO.getLocation().isEmpty()) && unity.getLocation()!= null)
            recoverItemDTO.setLocation(unity.getLocation());

        return nasphService.recoverItem(recoverItemDTO);
    }

    public PaginateResponseDTO<Object> listUnitiesForItemRecover(Integer pageNo, Integer pageSize, List<String> sortAsc, List<String> sortDesc, String term) {
        Pageable paging = getPageable(pageNo, pageSize, sortAsc, sortDesc);

        List<String> situationsUnity = List.of(SituationEnum.BXU.getCod(),SituationEnum.BXE.getCod(),SituationEnum.BXO.getCod(),SituationEnum.BXP.getCod());

        return getUnitiesBySitAndDepartment(term, paging, situationsUnity);
    }

    public UnityToReprocessSapDTO sapUnityReprocessable(String id) throws NotReprocessableUnityException {
        ReprocessableUnityDTO reprocessableUnityDTO = unityRepository.findReprocessableUnity(id);
        return reprocessUnityService.reprocessUnity(reprocessableUnityDTO);
    }

    public MessageResponseDTO reprocessSapUnity(UnityToReprocessSapDTO unityToReprocessSapDTO){

        ReprocessSapDTO reprocessSapDTO = ReprocessSapDTO.fromSapReprocessUnity(unityToReprocessSapDTO);
        ReprocessableUnityDTO reprocessableUnityDTO = unityRepository.findReprocessableUnity(unityToReprocessSapDTO.getUnityId());
        reprocessSapDTO.setOperation(reprocessableUnityDTO.getOperation());
        reprocessSapDTO.setIdInformaticsRec(reprocessableUnityDTO.getIdTbrec());
        reprocessSapDTO.setRegisterReason(reprocessableUnityDTO.getRegisterReason());

        return nasphService.reprocessSapUnity(reprocessSapDTO);
    }

    public List<WarrantyViewDTO> getWarranty(String id) throws UnityNotFoundException {
        verifyIfExists(id);
        List<WarrantyView> warrantyView  = warrantyViewRepository.findByUnityId(id);
        WarrantyViewMapper warrantyViewMapper = WarrantyViewMapper.INSTANCE;
        return warrantyView.stream().map(warrantyViewMapper::toDTO).collect(Collectors.toList());

    }

    public List<RepairTicketDTO> getTickets(String id) throws UnityNotFoundException {
        verifyIfExists(id);
        List<RepairTicket> repairTickets = repairTicketRepository.findByUnityId(id);
        RepairTicketMapper mapper = RepairTicketMapper.INSTANCE;
        return repairTickets.stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @SneakyThrows
    public MessageResponseDTO generalAcceptance(UnityAcceptanceDTO unityAcceptanceDTO) throws UnityNotFoundException, UnityException {
        Unity unity =  verifyIfExists(unityAcceptanceDTO.getUnityId());
        verifySapError(unity);
        unityAcceptanceDTO.setUserId(Utils.getUser().getId());
        return nasphService.generalAcceptance(unityAcceptanceDTO);
    }

    @SneakyThrows
    public MessageResponseDTO changeBarcode(ChangeBarcodeDTO changeBarcode){
        verifyIfExists(changeBarcode.getOldbarcode());
        Optional<Unity> newBarcode = unityRepository.findById(changeBarcode.getNewBarcode());
        if(newBarcode.isPresent())
            throw new IllegalArgumentException(MessageUtils.BARCODE_ALREADY_EXISTS.getDescription());
        return nasphService.changeBarcode(changeBarcode);
    }

    @Transactional
    public byte[] trackingRecord(List<UnityDTO> unities) {
        List<String> barcodes = unities.stream().map(UnityDTO::getId).collect(Collectors.toList());
        List<TrackingRecordDTO> trackingRecordDTO = unityRepository.getTrackingRecord(barcodes);
        String abrevUser = Utils.getUser().getId().substring(0,2);
        try {
            trackingRecordDTO.forEach(tr->{
                String sequence = unityRepository.nextFasSequence();
                tr.setSequence(abrevUser + sequence);
                BarcodeFAS barcodeFAS = BarcodeFAS.builder().unity(Unity.builder().id(tr.getBarcode()).build()).id(tr.getSequence()).build();
                barcodeFASRepository.save(barcodeFAS);
            });
            Map<String, Object> parameters = new HashMap<>();
            return reportService.trackingRecordReport(trackingRecordDTO, parameters);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }

    }

    public byte[] unitExtractionReport(UnitExtractionDTO unitExtractionDTO) {
        try {
            List<Unity> unities = unityRepository.findForExtraction(unitExtractionDTO);
            if(unities.isEmpty())
                throw new IllegalArgumentException(MessageUtils.EMPTY_REPORT.getDescription());
            Map<String, Object> parameters = new HashMap<>();
            List<UnitExtractionReportDTO> reportData = new ArrayList<>();
            unities.forEach(u->{
                UnitExtractionReportDTO reportDTO = extractUnityFactory.createExtractUnity(u);
                reportData.add(reportDTO);
            });
            return reportService.unitExtractionReport(reportData, parameters);
        }catch (Exception e){
            throw new IllegalArgumentException(MessageUtils.ERROR_REPORT.getDescription());
        }
    }

}
