package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "TECNICO_MODELOS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelTechnician implements Serializable {

    @EmbeddedId
    private ModelTechnicianID id;

    @Column(name = "TM_PRIORIDADE")
    private Long priority;
}
