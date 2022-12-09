package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "TECNICA")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Technique implements Serializable {

    private static final long serialVersionUID = -329041594341560925L;
    @Id
    @Column(name = "TC_CODIGO", nullable = false)
    private String id;

    @Column(name = "TC_DESCRICAO", nullable = false)
    private String description;

    @Column(name = "TC_TIPO_TECNICA", nullable = false)
    private String techniqueType;
}
