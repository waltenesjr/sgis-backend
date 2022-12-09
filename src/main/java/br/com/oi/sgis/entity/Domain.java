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
@Table(name = "DOMINIO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Domain implements Serializable {

    private static final long serialVersionUID = 1141088337644994792L;
    @EmbeddedId
    private DomainID domainID;

    @Column(name = "COD_CHAVE_DO", nullable = false)
    private String key;

    @Column(name = "DESC_CHAVE_DO", nullable = false)
    private String description;
}
