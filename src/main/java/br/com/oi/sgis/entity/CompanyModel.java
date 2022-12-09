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
@Table(name = "EMPRESA_MODELOS")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CompanyModel implements Serializable {

    private static final long serialVersionUID = 578484585196591628L;
    @EmbeddedId
    private CompanyModelID id;

    @Column(name = "EM_PRIORIDADE")
    private Long priority;

}
