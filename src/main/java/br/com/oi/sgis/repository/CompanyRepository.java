package br.com.oi.sgis.repository;

import br.com.oi.sgis.dto.EmitProofProviderDTO;
import br.com.oi.sgis.dto.EmitProofProviderReportDTO;
import br.com.oi.sgis.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {

    @Query("select c from Company c where upper(c.id) like %:search% " +
            "or upper(c.companyName) like %:search% or upper(c.tradeName) like %:search% " +
            "or upper(c.stateRegistration) like %:search% ")
    Page<Company> findLike(@Param("search") String search, Pageable paging);

    @Query(value = "SELECT * FROM EMPRESAS WHERE E_CGC_CPF <> E_CNPJ_CPF AND E_CGC_CPF LIKE '0000%' ORDER BY E_CGC_CPF desc limit 1", nativeQuery = true)
    Company findTopOrderByIdDesc();

    Optional<Company> findTopByCnpjCpfEquals(String cnpjCpf);

    @Query("select c from Company c where (upper(c.id) like %:search% " +
            "or upper(c.companyName) like %:search% or upper(c.tradeName) like %:search% " +
            "or upper(c.stateRegistration) like %:search%) and c.active = true and c.holding = true")
    Page<Company> findAllByActiveTrueAndHoldingTrue(@Param("search") String search, Pageable paging);

    @Query("select c from Company c where (upper(c.id) like %:search% " +
            "or upper(c.companyName) like %:search% or upper(c.tradeName) like %:search% " +
            "or upper(c.stateRegistration) like %:search%) and c.client = true")
    Page<Company> findAllClient(@Param("search") String search, Pageable paging);

    @Query("select new br.com.oi.sgis.dto.EmitProofProviderReportDTO(a.id, a.tradeName, b.id, b.tradeName, u.responsible.id, e.id, e.description, u.id ,u.situationDateChange) " +
            " from Unity u inner join u.providerResponsible a left join u.client b inner join u.unityCode e " +
            " where u.providerResponsible.id = :#{#emitProofProviderDTO.providerId}  " +
            " and u.situationDateChange between :#{#emitProofProviderDTO.initialDate} and :#{#emitProofProviderDTO.finalDate}  " +
            " and u.responsible.id like %:#{#emitProofProviderDTO.responsibleId} and u.situationCode.id = 'PRE' " +
            " and u.barcodeParent is null " +
            "  order by a.id,  e.id")
    List<EmitProofProviderReportDTO> emitProofProvider(EmitProofProviderDTO emitProofProviderDTO);

    @Query("select c from Company c where c.id not in (select company.id from Parameter) " +
            " and ( upper(c.id) like %:search% " +
            " or upper(c.companyName) like %:search% " +
            " or upper(c.tradeName) like %:search% " +
            " or upper(c.stateRegistration) like %:search%) ")
    Page<Company> findAllCompanyWithoutParameter(@Param("search") String search, Pageable paging);

    @Query("select c from Company c where c.id in (select distinct ts.cgcCpfCompany from TechnicalStaff ts where ts.cgcCpfCompany is not null)" +
            " and (upper(c.id) like %:search% " +
            "or upper(c.companyName) like %:search% or upper(c.tradeName) like %:search% " +
            "or upper(c.stateRegistration) like %:search%) ")
    Page<Company> findForUsersExtraction(@Param("search") String search, Pageable paging);
}
