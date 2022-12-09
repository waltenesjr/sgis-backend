package br.com.oi.sgis.service;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.repository.ProceduresRepository;
import br.com.oi.sgis.service.validator.Validator;
import br.com.oi.sgis.util.MessageUtils;
import br.com.oi.sgis.util.Utils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class NasphService {
    private Validator<TransfPropertyDTO> transfPropertyValidator;
    private Validator<RepoPropertyDTO> repoPropertyValidator;
    private Validator<UnitySituationDTO> unitySituationDTOValidator;
    private Validator<UnitySwapDTO> unitySwapDTOValidator;
    private Validator<PlanInstallationDTO> planInstallationDTOValidator;
    private Validator<UnityWriteOffDTO> writeOffDTOValidator;
    private Validator<RecoverItemDTO> recoverItemDTOValidator;
    private final ProceduresRepository repository;

    public MessageResponseDTO transfProperty(UnityDTO unityDTO){

        TransfPropertyDTO transfPropertyDTO = TransfPropertyDTO.builder()
                .pendency("N")
                .idDepDestination(unityDTO.getDestination().getId())
                .idUser(Utils.getUser().getId())
                .idUnity(unityDTO.getId())
                .build();

        transfPropertyValidator.validate(transfPropertyDTO);

        try {
            repository.transfProperty(transfPropertyDTO);
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.UNITY_PROP_TRANSF_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }catch (NegativeArraySizeException e){
            //Ignora o erro de retorno qndo funciona a procedure
        }
        return  createMessageResponse(String.format(MessageUtils.UNITY_PROP_TRANSF_SUCCESS.getDescription(), unityDTO.getId()));
    }

    private MessageResponseDTO createMessageResponse(String message) {
        return MessageResponseDTO.builder().message(message).title("Sucesso").status(HttpStatus.OK).build();
    }

    public MessageResponseDTO repoProperty(UnityDTO unityDTO) {

        RepoPropertyDTO repoPropertyDTO = RepoPropertyDTO.builder()
                .location(unityDTO.getLocation())
                .idUser(Utils.getUser().getId())
                .idUnity(unityDTO.getId())
                .idStation(unityDTO.getStation().getId())
                .originUf(null)
                .build();

        repoPropertyValidator.validate(repoPropertyDTO);

        try {
            repository.repoProperty(repoPropertyDTO);
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.UNITY_PROP_REPO_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }catch (NegativeArraySizeException e){
            //Ignora o erro de retorno qndo funciona a procedure
        }

        return  createMessageResponse(String.format(MessageUtils.UNITY_PROP_REPO_SUCCESS.getDescription(), unityDTO.getId()));
    }

    public MessageResponseDTO updateUnitySituation(UnitySituationDTO unitySituationDTO) {

        unitySituationDTOValidator.validate(unitySituationDTO);
        unitySituationDTO.setIdUser(Utils.getUser().getId());

        try {
            repository.updateUnitySituation(unitySituationDTO, unitySituationDTO.getSituation().getCod());
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.UNITY_UPDT_SIT_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }catch (NegativeArraySizeException e){
            //Ignora o erro de retorno qndo funciona a procedure
        }

        return  createMessageResponse(String.format(MessageUtils.UNITY_UPDT_SIT_SUCCESS.getDescription(), unitySituationDTO.getUnityId()));
    }

    public MessageResponseDTO updateUnitySwap(UnitySwapDTO unitySwapDTO) {
        unitySwapDTOValidator.validate(unitySwapDTO);
        String procedureReturn = "";
        try {

            repository.updateItemSwap(unitySwapDTO, Utils.getUser().getId(), procedureReturn);
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.UNITY_UPDT_SWAP_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }catch (NegativeArraySizeException e){
            //Ignora o erro de retorno qndo funciona a procedure
        }
        return  createMessageResponse(MessageUtils.UNITY_UPDT_SWAP_SUCCESS.getDescription());
    }

    public MessageResponseDTO updatePlanInstallation(PlanInstallationDTO planInstallationDTO){
        planInstallationDTOValidator.validate(planInstallationDTO);
        try {
            repository.planInstallation(planInstallationDTO, Utils.getUser().getId());
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.UNITY_PLAN_INSTALLATION_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }catch (NegativeArraySizeException e){
            //Ignora o erro de retorno qndo funciona a procedure
        }
        return  createMessageResponse(MessageUtils.UNITY_PLAN_INSTALLATION_SUCCESS.getDescription());
    }

    public MessageResponseDTO updateUnityWriteOff(UnityWriteOffDTO unityWriteOffDTO) {
        writeOffDTOValidator.validate(unityWriteOffDTO);
        try {
            repository.updateUnityWriteOff(unityWriteOffDTO, Utils.getUser().getId());
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.UNITY_WRITE_OFF_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }catch (NegativeArraySizeException e){
            //Ignora o erro de retorno qndo funciona a procedure
        }

        return  createMessageResponse(MessageUtils.UNITY_WRITE_OFF_SUCCESS.getDescription());
    }

    public MessageResponseDTO recoverItem(RecoverItemDTO recoverItemDTO) {
        recoverItemDTOValidator.validate(recoverItemDTO);

        try {
            repository.recoverItem(recoverItemDTO, Utils.getUser().getId());
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.UNITY_RECOVER_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }catch (NegativeArraySizeException e){
            //Ignora o erro de retorno qndo funciona a procedure
        }
        return  createMessageResponse(MessageUtils.UNITY_RECOVER_SUCCESS.getDescription());
    }


    public MessageResponseDTO reprocessSapUnity(ReprocessSapDTO reprocessSapDTO) {

        try {
            repository.reprocessSap(reprocessSapDTO);
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.UNITY_REPROCESS_SAP_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }catch (NegativeArraySizeException e){
            //Ignora o erro de retorno qndo funciona a procedure
        }
        return  createMessageResponse(MessageUtils.UNITY_REPROCESS_SAP_SUCCESS.getDescription());
    }

    public MessageResponseDTO acceptRepairTicket(AcceptTcktRepairNasphDTO acceptTicketRepairDTO) {
        try {
            repository.acceptTicket(acceptTicketRepairDTO);
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.REPAIR_TICKET_ACCEPT_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }catch (NegativeArraySizeException e){
            //Ignora o erro de retorno qndo funciona a procedure
        }
        return  createMessageResponse(MessageUtils.REPAIR_TICKET_ACCEPT_SUCCESS.getDescription());

    }

    public MessageResponseDTO generalAcceptance(UnityAcceptanceDTO unityAcceptanceDTO) {

        try {
            repository.generalAcceptance(unityAcceptanceDTO);
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.UNITY_ACCEPT_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }catch (NegativeArraySizeException e){
            //Ignora o erro de retorno qndo funciona a procedure
        }
        return  createMessageResponse(MessageUtils.UNITY_ACCEPT_SUCCESS.getDescription());

    }

    public MessageResponseDTO closeRepair(CloseRepairTickectDTO closeRepairTicketDTO) {
        try {
            repository.closeRepair(closeRepairTicketDTO.getUserId(), closeRepairTicketDTO.getBarcode(), closeRepairTicketDTO.getFinalSituation(), closeRepairTicketDTO.getLocation());
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.CLOSE_REPAIR_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }catch (NegativeArraySizeException e){
            //Ignora o erro de retorno qndo funciona a procedure
        }
        return  createMessageResponse(MessageUtils.CLOSE_REPAIR_SUCCESS.getDescription());
    }

    public String closeRepairSituation(String unityId) {
        try {
            return repository.finalSituationRepair(unityId);
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.CLOSE_REPAIR_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }
    }

    public String updateInterventionSituation(String brNumber) {
        try {
           return repository.interventionSituation(brNumber);
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.CLOSE_REPAIR_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }
    }

    public MessageResponseDTO devolutionRepair(DevolutionRepairTicketDTO dto) {
        try {
            repository.devolutionRepair(dto.getUserId(), dto.getBarcode(), dto.getDevolutionArea(), dto.getFiscalTelemar(), dto.getDevolutionNFdate(), dto.getTechnicianObs());
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.DEVOLUTION_REPAIR_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }catch (NegativeArraySizeException e){
            //Ignora o erro de retorno qndo funciona a procedure
        }
        return  createMessageResponse(MessageUtils.DEVOLUTION_REPAIR_SUCCESS.getDescription());
    }

    public MessageResponseDTO cancelRepair(String unityId, String userId) {

        try {
            repository.cancelRepair(userId, unityId);
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.DEVOLUTION_REPAIR_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }
        return  createMessageResponse(MessageUtils.CANCEL_REPAIR_SUCCESS.getDescription());
    }

    public MessageResponseDTO transferTechnicalStaff(TransferTechnicalDTO transferTechnicalDTO) {
        String pendency = Boolean.TRUE.equals(transferTechnicalDTO.getGeneratePendency()) ? "S" : "N";
        String userId = Utils.getUser().getId();
        try {
            repository.transferTechnicalStaff(userId, transferTechnicalDTO.getBarcode(), transferTechnicalDTO.getTechnicianId(), pendency);
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.TRANSFER_TECHNICIAN_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }
        return  createMessageResponse(MessageUtils.TRANSFER_TECHNICIAN_SUCCESS.getDescription());
    }

    public MessageResponseDTO transferProvider(TransferProviderDTO transferProvider) {
        String pendency = Boolean.TRUE.equals(transferProvider.getGeneratePendency()) ? "S" : "N";
        String userId = Utils.getUser().getId();
        try {
            repository.transferProvider(userId, transferProvider.getBarcode(), transferProvider.getProviderId(), pendency);
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.TRANSFER_PROVIDER_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }
        return  createMessageResponse(MessageUtils.TRANSFER_PROVIDER_SUCCESS.getDescription());
    }

    public MessageResponseDTO instClientByProvider(ClientInstByProviderDTO clientInstByProviderDTO) {
        String userId = Utils.getUser().getId();
        try {
            repository.instClientByProvider(userId, clientInstByProviderDTO.getBarcode(), clientInstByProviderDTO.getClientId(), clientInstByProviderDTO.getAddressId());
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.INSTA_CLIENT_PROVIDER_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }
        return  createMessageResponse(MessageUtils.INSTA_CLIENT_PROVIDER_SUCCESS.getDescription());
    }

    public MessageResponseDTO instClientByTechnician(ClientInstByProviderDTO clientInstByProviderDTO, String provider, String userId) {
        try {
            repository.instClientByTechnician(userId, clientInstByProviderDTO.getBarcode(), clientInstByProviderDTO.getClientId(), provider, clientInstByProviderDTO.getAddressId());
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.INSTA_CLIENT_TECHNICIAN_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }
        return  createMessageResponse(MessageUtils.INSTA_CLIENT_TECHNICIAN_SUCCESS.getDescription());

    }

    public MessageResponseDTO changeBarcode(ChangeBarcodeDTO changeBarcode) {
        try {
            repository.changeBarcode(Utils.getUser().getId(), changeBarcode.getOldbarcode(), changeBarcode.getNewBarcode());
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.CHANGE_BARCODE_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }
        return  createMessageResponse(MessageUtils.CHANGE_BARCODE_SUCCESS.getDescription());

    }

    public MessageResponseDTO estimateExternalOutput(EstimateExternalOutputDTO estimateExternalOutputDTO) {
        try {
            repository.estimateExternalOutput(Utils.getUser().getId(), estimateExternalOutputDTO.getEstimateId(), estimateExternalOutputDTO.getFiscalNoteDate(), estimateExternalOutputDTO.getFiscalNote());
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.ESTIMATE_EXTERNAL_OUTPUT_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }
        return  createMessageResponse(MessageUtils.ESTIMATE_EXTERNAL_OUTPUT_SUCCESS.getDescription());
    }

    public void cancelItemEstimate(String barcode) {
        String user = Utils.getUser().getId();
        try {
            repository.cancelItemEstimate(user, barcode);
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.ITEM_ESTIMATE_CANCEL_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }

    }

    public void approveItemEstimate(String barcode, String provider, BigDecimal value) {
        String user = Utils.getUser().getId();
        try {
            repository.approveItemEstimate(user, barcode, provider, value);
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.ITEM_ESTIMATE_APPROVE_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }
    }

    public MessageResponseDTO cancelEstimate(String estimateId) {
        String user = Utils.getUser().getId();
        try {
            repository.cancelEstimate(user, estimateId);
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.ESTIMATE_CANCEL_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }
        return  createMessageResponse(MessageUtils.ESTIMATE_CANCEL_SUCCESS.getDescription());

    }

    public MessageResponseDTO returnExternalRepair(RepairExternalReturnDTO repairExternalReturnDTO) {

        String user = Utils.getUser().getId();
        String substitution = Boolean.TRUE.equals(repairExternalReturnDTO.getSubstitution()) ? "S" : "N";
        try {
            repository.returnExternalRepair(user, repairExternalReturnDTO.getBarcode(), repairExternalReturnDTO.getArrivalDate(), repairExternalReturnDTO.getFiscalNoteDate(),
                    repairExternalReturnDTO.getFiscalNote(), substitution,repairExternalReturnDTO.getNewBarcode());
        }catch (JpaSystemException e){
            throw new IllegalArgumentException(MessageUtils.EXTERNAL_REPAIR_RETURN_ERROR.getDescription().concat(" - ").concat(Objects.requireNonNull(e.getRootCause()).getMessage()));
        }
        return  createMessageResponse(MessageUtils.EXTERNAL_REPAIR_RETURN_SUCCESS.getDescription());

    }
}
