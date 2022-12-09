package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.ReprocessableUnityDTO;
import br.com.oi.sgis.dto.TrackingRecordDTO;
import br.com.oi.sgis.dto.UnitExtractionDTO;
import br.com.oi.sgis.dto.UnityDTO;
import br.com.oi.sgis.entity.Unity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UnityRepository extends JpaRepository<Unity, String>, SummaryItemRepository,
        SituationItemRepository,ItensByStealReasonRepository, GeneralItensRepository, RegisteredItensRepository {

    @Query("select u from Unity u left join u.unityCode uc left join u.responsible r" +
            " where upper(u.id) like %:search% or upper(u.serieNumber) like %:search%" +
            " or upper(u.registerReason) like %:search% or upper(uc.id) like %:search%  " +
            " or upper(uc.description) like %:search% or upper(r.id) like %:search%" +
            " or upper(r.description) like %:search%  ")
    Page<Unity> findLike(@Param("search") String search, Pageable pageable);


    @Query("select u from Unity u where u.situationCode.id in (:situations)")
    Page<Unity> findAllBySituationCodeContains(@Param("situations") List<String> situations, Pageable paging);

    @Query("select u from Unity u  left join u.unityCode uc left join u.responsible r " +
            " where (upper(u.id) like %:search% or upper(u.serieNumber) like %:search% " +
            " or upper(u.registerReason) like %:search% or upper(uc.id) like %:search% " +
            " or upper(uc.description) like %:search% or upper(r.id) like %:search% " +
            " or upper(r.description) like %:search%) and u.situationCode.id in (:situations)")
    Page<Unity> findAllBySituationCodeContainsLike(@Param("search") String search, @Param("situations") List<String> situations, Pageable paging);

    @Query("select u from Unity u where u.situationCode.id not in (:situations) and upper(u.situationCode.id) not" +
            " like 'BX%' and u.responsible.id = :userDepartment and u.destination = :userDepartment")
    Page<Unity> findAllForPropertyRepos(@Param("situations") List<String> situations, @Param("userDepartment") String userDepartment, Pageable paging);

    @Query("select u from Unity u  left join u.unityCode uc left join u.responsible r " +
            " where upper(u.id) like %:search% or upper(u.serieNumber) like %:search% " +
            " or upper(u.registerReason) like %:search% or upper(uc.id) like %:search% " +
            " or upper(uc.description) like %:search% or upper(r.id) like %:search% " +
            " or upper(r.description) like %:search% and u.situationCode.id not in (:situations) " +
            " and u.situationCode.id not like 'BX%' and r.id = :userDepartment and u.destination = :userDepartment")
    Page<Unity> findAllForPropertyReposContains(@Param("search") String search, @Param("situations") List<String> situations,@Param("userDepartment") String userDepartment, Pageable paging);

    @Query("select u from Unity u where u.situationCode.id in (:situations) and u.responsible.id = :userDepartment")
    Page<Unity> findAllBySituationCodeContainsAndDepartment(@Param("situations") List<String> situations,@Param("userDepartment") String userDepartment, Pageable paging);

    @Query("select u from Unity u  left join u.unityCode uc left join u.responsible r " +
            " where (upper(u.id) like %:search% or upper(u.serieNumber) like %:search% " +
            " or upper(u.registerReason) like %:search% or upper(uc.id) like %:search% " +
            " or upper(uc.description) like %:search% or upper(r.id) like %:search% " +
            " or upper(r.description) like %:search%) and u.situationCode.id in (:situations) and r.id = :userDepartment")
    Page<Unity> findAllBySituationCodeContainsAndDepartment(@Param("search") String search, @Param("situations") List<String> situations, @Param("userDepartment") String userDepartment, Pageable paging);

    @Query("select distinct new br.com.oi.sgis.dto.ReprocessableUnityDTO(tr.id," +
            "tr.canBeReprocessed, tr.operation, te.reasonForWriteOff, te.registerReason, u.id," +
            "u.unityCode.id, u.serieNumber,u.orderNumber, u.orderItem, u.reservationNumber," +
            "u.reservationItem,u.fixedNumber, u.fixedSubnumber, te.spareCenterId, te.status," +
            " u.originUf, u.accountantCompany,u.activeClass ) " +
            "from InformaticsRec tr, InformaticsEnv te, Unity u " +
            "where tr.unity.id =:idUnity and tr.unity.id = te.unity.id " +
            "and tr.id in (select max(id) from InformaticsRec where unity.id = tr.unity.id) " +
            "and tr.id = te.id and te.unity.id = u.id")
    ReprocessableUnityDTO findReprocessableUnity(@Param("idUnity")String idUnity);

    @Modifying @Transactional
    @Query("update Unity u set u.serieNumber = :#{#unityDTO.serieNumber},  " +
            " u.station.id = :#{(#unityDTO.station != null ? #unityDTO.station.id : null)}, u.location = :#{#unityDTO.location}, u.value = :#{#unityDTO.value}," +
            " u.tipping = :#{#unityDTO.tipping}, u.observation = :#{#unityDTO.observation}, u.fiscalDocument.id.docDate = :#{(#unityDTO.fiscalDocument != null ?#unityDTO.fiscalDocument.docDate : null)}," +
            " u.fiscalDocument.id.docNumber = :#{(#unityDTO.fiscalDocument != null ?#unityDTO.fiscalDocument.docNumber : null)}," +
            " u.fiscalDocument.id.cgcCPf.id = :#{(#unityDTO.fiscalDocument != null ?#unityDTO.fiscalDocument.companyId : null)} where u.id =:#{#unityDTO.id}")
    void updateUnity(@Param("unityDTO")UnityDTO unityDTO);

    @Query(value = "select u from Unity u where u.box.id = :boxId and u.barcodeParent is null order by u.situationCode.id")
    List<Unity> findUnityByBox(@Param("boxId") String boxId);

    @Modifying
    @Query("update Unity u set u.box = null where u.id in (:unitiesToRemove)")
    void removeFromBox(@Param("unitiesToRemove") List<String> unitiesToRemove);

    @Query("select u from Unity u where u.barcodeParent =:unityId")
    List<Unity> findUnitiesComposition(@Param("unityId")String unityId);

    @Modifying @Transactional
    @Query("update Unity u set u.barcodeParent = :modelId, u.responsible.id = :responsibleID where u.id =:itemId")
    void updateComposition(@Param("modelId") String modelId, @Param("itemId") String itemId, @Param("responsibleID") String responsibleID);

    @Query("select distinct new br.com.oi.sgis.dto.TrackingRecordDTO(a.id, a.serieNumber, b.id, " +
            " b.mnemonic, b.description, f.description, g.companyName, a.accountantCompany, a.activeClass) " +
            " from Unity a left join a.unityCode b left join a.responsible c " +
            " left join b.equipModelCode d left join d.equipamentType e " +
            " left join e.technique f left join b.company g" +
            " where a.id in (:barcodes)")
    List<TrackingRecordDTO> getTrackingRecord(@Param("barcodes") List<String> barcodes);

    @Query(value = "SELECT SEQ_NUM_FAS.NEXTVAL FROM dual", nativeQuery = true)
    String nextFasSequence();

    @Query("select u from Unity u where (u.situationCode.id = :#{#dto.situation} or (:#{#dto.situation} is null or :#{#dto.situation}  = '' ))" +
            " and (u.responsible.id = :#{#dto.responsible} or (:#{#dto.responsible} is null or :#{#dto.responsible}  = '' )) " +
            " and (u.uf.id = :#{#dto.uf} or (:#{#dto.uf} is null or :#{#dto.uf}  = '' )) " +
            " and (u.unityCode.equipModelCode.equipamentType.technique.id = :#{#dto.technic} or (:#{#dto.technic} is null or :#{#dto.technic}  = '' ))")
    List<Unity> findForExtraction(@Param("dto") UnitExtractionDTO dto);
}
