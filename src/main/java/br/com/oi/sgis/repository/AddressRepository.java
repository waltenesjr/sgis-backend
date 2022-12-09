package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, String> {
    @Query("select a from Address a where upper(a.id) like %:search% " +
            "or upper(a.description) like %:search%  or upper(a.addressDescription) like %:search% " +
            "or upper(a.city) like %:search% or upper(a.district) like %:search% ")
    Page<Address> findLike(@Param("search") String search, Pageable paging);

    @Query("select a from Address a left join a.cgcCpf e  where upper(a.id) = :search " +
            "or upper(e.id) = :search")
    Optional<Address> findTopByIdOrCgcCpfId(@Param("search") String search);

    Address findTopByOrderByIdDesc();

    List<Address> findAllByCgcCpfId(String cgcCpf);
}
