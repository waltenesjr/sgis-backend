package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Domain;
import br.com.oi.sgis.entity.DomainID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DomainRepository extends JpaRepository<Domain, DomainID> {

    List<Domain> findAllByDomainID_Id(String id);



    @Query(" select d from Domain d where d.domainID.id = :id and d.description in (:descriptions) or d.domainID.id in (:descriptions)")
    List<Domain> findDistinctByDescriptionInAndDomainID_Id(List<String> descriptions, String id);
}
