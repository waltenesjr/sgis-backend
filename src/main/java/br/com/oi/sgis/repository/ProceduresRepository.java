package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.entity.Unity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ProceduresRepository extends JpaRepository<Unity, String>, ProcedureSitOutRepository {


    @Query(value = "call p_trans_prop2(:#{#transfPropertyDTO.idUser}, :#{#transfPropertyDTO.idUnity}, :#{#transfPropertyDTO.idDepDestination}, :#{#transfPropertyDTO.pendency})", nativeQuery = true)
    void transfProperty(@Param("transfPropertyDTO") TransfPropertyDTO transfPropertyDTO);

    @Query(value = "call p_retomada_prop(:#{#repoPropertyDTO.idUser}, :#{#repoPropertyDTO.idUnity}, :#{#repoPropertyDTO.idStation}, :#{#repoPropertyDTO.location},:#{#repoPropertyDTO.originUf} )", nativeQuery = true)
    void repoProperty(@Param("repoPropertyDTO") RepoPropertyDTO repoPropertyDTO);

    @Query(value = "call p_alter_sit(:#{#unitySituationDTO.unityId}, :#{#unitySituationDTO.reservationId},  :situation, :#{#unitySituationDTO.idUser}, :#{#unitySituationDTO.location},:#{#unitySituationDTO.stationId} )", nativeQuery = true)
    void updateUnitySituation(@Param("unitySituationDTO")UnitySituationDTO unitySituationDTO, @Param("situation") String situation);

    @Query(value = "call p_swap_unidade(:#{#unitySwapDTO.unityId}, :#{#unitySwapDTO.unityNewBarcode},  :#{#unitySwapDTO.newUnityCode}, :#{#unitySwapDTO.newSerieNumber}, :userId, :ret )", nativeQuery = true)
    void updateItemSwap(@Param("unitySwapDTO") UnitySwapDTO unitySwapDTO, @Param("userId") String userId, String ret);

    @Query(value = "call p_baixa_un(:userId, :#{#unityWriteOffDTO.unityId},  :#{#unityWriteOffDTO.situationID}, :#{#unityWriteOffDTO.reasonForWriteOff.reason}, :#{#unityWriteOffDTO.technicalReport} )", nativeQuery = true)
    void updateUnityWriteOff(@Param("unityWriteOffDTO") UnityWriteOffDTO unityWriteOffDTO, @Param("userId") String userId);

    @Query(value = "call p_recuperacao_un_bx(:userId, :#{#recoverItemDTO.unityId},  :#{#recoverItemDTO.location}, :#{#recoverItemDTO.stationId})", nativeQuery = true)
    void recoverItem(@Param("recoverItemDTO") RecoverItemDTO recoverItemDTO, @Param("userId") String userId);

    @Query(value = "call p_reprocessa_sap(:#{#reprocessSapDTO.userId}, :#{#reprocessSapDTO.unityId},  :#{#reprocessSapDTO.unityCode}, :#{#reprocessSapDTO.orderNumber}, :#{#reprocessSapDTO.orderItem}, :#{#reprocessSapDTO.reservationNumber}, :#{#reprocessSapDTO.reservationItem}, " +
            ":#{#reprocessSapDTO.fixedNumber}, :#{#reprocessSapDTO.fixedSubnumber}, :#{#reprocessSapDTO.serieNumber},:#{#reprocessSapDTO.responsibleId} , :#{#reprocessSapDTO.originUf},:#{#reprocessSapDTO.operation} , :#{#reprocessSapDTO.idInformaticsRec}, :#{#reprocessSapDTO.registerReason})", nativeQuery = true)
    void reprocessSap(@Param("reprocessSapDTO") ReprocessSapDTO reprocessSapDTO);

    @Query(value = "call p_aceit_bilh_rep(:#{#acceptTicketRepairDTO.user}, :#{#acceptTicketRepairDTO.unity},  :#{#acceptTicketRepairDTO.station}, :#{#acceptTicketRepairDTO.location})", nativeQuery = true)
    void acceptTicket(@Param("acceptTicketRepairDTO")AcceptTcktRepairNasphDTO acceptTicketRepairDTO);

    @Query(value = "call p_entrada(:#{#unityAcceptanceDTO.userId}, :#{#unityAcceptanceDTO.unityId},  :#{#unityAcceptanceDTO.location}, :#{#unityAcceptanceDTO.stationId})", nativeQuery = true)
    void generalAcceptance(@Param("unityAcceptanceDTO") UnityAcceptanceDTO unityAcceptanceDTO);

    @Query(value = "call p_instal_planta(:userId, :#{#planInstallationDTO.unityId}, :#{#planInstallationDTO.central},  :#{#planInstallationDTO.baBdCode}, :#{#planInstallationDTO.stationId}, " +
            ":#{#planInstallationDTO.installationReason.lostReason}, :#{#planInstallationDTO.boNumber}, :#{#planInstallationDTO.sinisterNumber}, :#{#planInstallationDTO.technicianId})", nativeQuery = true)
    void planInstallation(@Param("planInstallationDTO") PlanInstallationDTO planInstallationDTO, @Param("userId") String userId);

    @Procedure(value = "p_fechamento_reparo")
    void closeRepair(@Param("usuario") String user ,@Param("codigo_barras") String barcode, @Param("situacao_nova") String newSituation, @Param("locacao") String location);

    @Procedure(value = "p_sit_final_rep", outputParameterName = "sit_final")
    String finalSituationRepair(@Param("codigo_barras") String unityId);

    @Procedure(value = "p_dev_bilh_rep")
    void devolutionRepair(@Param("usuario")  String userId, @Param("codigo_barras")  String barcode, @Param("depto_destino")  String devolutionArea, @Param("NF_DEVOL")  String fiscalTelemar,
                          @Param("NF_DATA") LocalDateTime devolutionNFdate,  @Param("OBS_DEVOL") String technicianObs);

    @Procedure(value = "p_dev_bilh_rep")
    void cancelRepair(@Param("usuario") String userId,@Param("codigo_barras")  String unityId);

    @Procedure(value = "p_trans_tec")
    void transferTechnicalStaff(@Param("usuario")String userId, @Param("codigo_barras")String barcode, @Param("tecnico")String technicianId, @Param("flag_pend") String pendency);

    @Procedure(value = "p_transf_prestador")
    void transferProvider(@Param("usuario")String userId, @Param("codigo_barras")String barcode, @Param("prestador") String providerId, @Param("flag_pend")String pendency);

    @Procedure(value = "p_instal_cliente")
    void instClientByProvider(@Param("usuario")String userId,@Param("codigo_barras") String barcode, @Param("cliente")String clientId, @Nullable @Param("endereco")String addressId );

    @Procedure(value = "p_instal_cliente_tecnico")
    void instClientByTechnician(@Param("usuario")String userId, @Param("codigo_barras")String barcode,@Param("cliente") String clientId,@Param("prestador") String provider, @Param("endereco") String addressId);

    @Procedure(value = "p_troca_cod_barras")
    void changeBarcode(@Param("usuario")String userId, @Param("codigo_barras")String oldbarcode,@Param("novo_cod") String newBarcode);

    @Procedure(value = "p_orca_envio")
    void estimateExternalOutput(@Param("usuario")String id, @Param("num_orca")String estimateId, @Param("data_nf")LocalDateTime fiscalNoteDate,@Param("num_nf") String fiscalNote);

    @Procedure(value = "p_orca_cancela")
    void cancelItemEstimate(@Param("usuario")String id, @Param("cod_barras")String barcode);

    @Procedure(value = "p_orca_aceita")
    void approveItemEstimate(@Param("usuario")String user, @Param("cod_barras")String barcode, @Param("orc_fornecedor")String provider, @Param("valor")BigDecimal value);

    @Procedure(value = "p_orca1_cancela")
    void cancelEstimate(@Param("usuario")String user, @Param("num_orc")String estimateId);

    @Procedure(value = "p_orca_retorno")
    void returnExternalRepair(@Param("usuario")String user,@Param("codigo_barras") String barcode,@Param("data_chegada") LocalDateTime arrivalDate, @Param("data_nf")LocalDateTime fiscalNoteDate,
                              @Param("num_nf")String fiscalNote,@Param("substituicao") String substitution, @Param("codigo_barras_up")String newBarcode);

//    @Procedure("p_redisit_bilhete")
//    Map<String, String> interventionSituation(@Param(value = "numbr") String brNumber, @Param("sit_final")String sit_final, @Param("contrato_out")String contrato_out, @Param("empresa_out")String empresa_out, @Param("tecnico_out")String tecnico_out);
}
