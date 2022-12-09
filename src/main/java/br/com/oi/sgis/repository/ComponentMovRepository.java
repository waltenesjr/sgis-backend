package br.com.oi.sgis.repository;

import br.com.oi.sgis.entity.ComponentMov;
import br.com.oi.sgis.entity.ComponentMovID;
import br.com.oi.sgis.entity.DepartmentComponentID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ComponentMovRepository extends JpaRepository<ComponentMov, ComponentMovID> {

    @Query(value = "select cm from ComponentMov cm where cm.ticketIntervention.id.sequence in (:sequences) ")
    List<ComponentMov> findAllByTicketIntervention(@Param("sequences") List<Long> sequences);

    @Query(value = "SELECT DECODE(MAX(CM_SEQ),NULL,0,MAX(CM_SEQ))+1 FROM COMPONENTES_MOV", nativeQuery = true)
    Long getNextSequence();

    @Modifying @Transactional
    @Query(value = "INSERT INTO COMPONENTES_MOV (CM_COD_COMP, CM_SIGLA, CM_SEQ, " +
            "             CM_TECNICO, CM_COD_TIPO_MOV, CM_DATA, CM_QUANT, CM_DOCUMENTO, CM_NUM_BR, CM_SEQ_IB, CM_SALDO_ANT ) VALUES " +
            "            ( :componentId, :departmentId,:compSequence,:technicianId,'USO', SYSDATE ,:qtd,:brNumber,:brNumber,:sequence, '0') " , nativeQuery = true)
    void saveComponent(@Param("componentId") String componentId, @Param("departmentId") String departmentId, @Param("technicianId") String technicianId,
                                     @Param("qtd") Long qtd, @Param("brNumber") String brNumber, @Param("sequence") Long sequence,
                                     @Param("compSequence") Long compSequence);

    @Modifying @Transactional
    @Query(value = "update ComponentMov cm set cm.quantity = :quantity where cm.id.sequence =:sequence and" +
            " cm.id.departmentComponent.id.department.id = :#{#id.department.id} and  cm.id.departmentComponent.id.component.id = :#{#id.component.id}")
    void updateQuantity(Long quantity, DepartmentComponentID id, Long sequence);
}
