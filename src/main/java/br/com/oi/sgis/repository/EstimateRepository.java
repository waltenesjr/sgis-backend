package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Estimate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstimateRepository extends JpaRepository<Estimate, String> {

    @Query("select e from Estimate e where e.sendDate is null and (upper(e.id) like %:search% or " +
            " upper(e.department.id) like %:search% or  upper(e.department.description) like %:search% or " +
            " upper(e.contract.id) like %:search% )")
    Page<Estimate> findLikeOpen(@Param("search") String search, Pageable paging);

    @Query("select e from Estimate e where e.sendDate is not null and (upper(e.id) like %:search% or " +
            " upper(e.department.id) like %:search% or  upper(e.department.description) like %:search% or " +
            " upper(e.contract.id) like %:search% )")
    Page<Estimate> findLikeSent(@Param("search") String search, Pageable paging);

    @Query("select e from Estimate e where  (upper(e.id) like %:search% or " +
            " upper(e.department.id) like %:search% or  upper(e.department.description) like %:search% or " +
            " upper(e.contract.id) like %:search% )")
    Page<Estimate> findLike(@Param("search") String search, Pageable paging);


    @Override
    @Query("select e from Estimate e where e.id = :id")
    Optional<Estimate> findById(String id);

    @Query(value = "select o.OR_NUM_ORC from ORCAMENTO o where o.OR_NUM_ORC like :uf% order by o.OR_NUM_ORC desc limit 1", nativeQuery = true)
    String findTop1ByIdDesc(@Param("uf") String uf);
}
