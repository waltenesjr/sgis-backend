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
import java.math.BigDecimal;

@Entity
@Table(name = "CONTRATOS_MODELOS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelContract implements Serializable {

    private static final long serialVersionUID = 100054656096300157L;

    @EmbeddedId
    private ModelContractID id;

    @Column(name = "CS_VALOR_REPARO")
    private BigDecimal value;
}
