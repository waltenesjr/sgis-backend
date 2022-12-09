package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.FiscalDocument;
import br.com.oi.sgis.entity.FiscalDocumentId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface FiscalDocumentRepository extends JpaRepository<FiscalDocument, FiscalDocumentId> {

    @Query("select fd from FiscalDocument fd where upper(fd.id.cgcCPf.id) like %:search%  or upper(fd.contract.id) like %:search%  or fd.id.docNumber like %:search% " +
            " or upper(fd.id.cgcCPf.companyName) like %:search%  or fd.id.docNumber like %:search% or fd.accountingCompany like %:search%  ")
    Page<FiscalDocument> findLike(@Param("search") String search, Pageable paging);

    @Query("select fd from FiscalDocument fd where (fd.id.docDate between :initialDate and :finalDate or fd.activationDate between :initialDate and :finalDate) and" +
            " upper(fd.id.cgcCPf.id) like %:search% or upper(fd.accountingCompany) like %:search%  or upper(fd.contract.id) like %:search%  " +
            " or upper(fd.id.cgcCPf.companyName) like %:search%  or fd.id.docNumber like %:search% or fd.accountingCompany like %:search%")
    Page<FiscalDocument> findLikeDateBetween(@Param("search") String search,
                                             @Param("initialDate") LocalDateTime initalDate,
                                             @Param("finalDate")LocalDateTime finalDate,Pageable paging);

    @Query("select fd from FiscalDocument fd where fd.id.docDate between :initialDate and :finalDate or fd.activationDate between :initialDate and :finalDate")
    Page<FiscalDocument>findAllByDateBetween(@Param("initialDate") LocalDateTime initalDate,
                                             @Param("finalDate")LocalDateTime finalDate, Pageable pageable);
}
