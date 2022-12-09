package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Model;
import br.com.oi.sgis.entity.ModelId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, ModelId> {

    @Query("select m from Model m where upper(m.description) like %:search% " +
            " or upper(m.id.modelCod) like %:search%  or upper(m.id.manufacturerCod.id) like %:search% ")
    Page<Model> findLike(@Param("search") String search, Pageable paging);

    @Query("select m from Model m where m.id.manufacturerCod.id = :manufacturerCod and m.id.modelCod = :modelCod")
    Optional<Model> findById(String manufacturerCod, Long modelCod);
}
