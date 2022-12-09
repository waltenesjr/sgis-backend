package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, String> {

    @Query("select c from Contract c left join  c.company p left join c.department d " +
            " where upper(c.id) like %:search% or upper(p.id) like %:search% " +
            " or upper(p.tradeName) like %:search% or upper(p.companyName) like %:search%  " +
            " or upper(d.id) like %:search% or upper(d.description) like %:search%")
    Page<Contract> findLike(String search, Pageable paging);

    @Query("select c from Contract c, ModelContract md inner join  c.company p left join c.department d " +
            " where md.id.contract.id = c.id and (upper(c.id) like %:search% or upper(p.id) like %:search% " +
            " or upper(p.tradeName) like %:search% or upper(p.companyName) like %:search%  " +
            " or upper(d.id) like %:search% or upper(d.description) like %:search%)")
    Page<Contract>  listForwardRepair(String search, Pageable paging);
}
