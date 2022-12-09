package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.Time;
import br.com.oi.sgis.entity.TimeID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimeRepository extends JpaRepository<Time, TimeID> {

    @Query("select t from Time t where upper(t.id.intervention.id) like %:search%" +
            " or upper(t.id.intervention.description) like %:search% or upper(t.id.unityModel.id) like %:search%")
    Page<Time> findLike(@Param("search") String search, Pageable paging);

    @Query("select t from Time t where t.id.unityModel.id = :unityModelId and t.id.intervention.id = :interventionId")
    Optional<Time> findById(@Param("unityModelId")String unityModelId, @Param("interventionId")String interventionId);
}
