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
@Table(name = "TIPO_CAIXA")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoxType implements Serializable {
    private static final long serialVersionUID = -8138999373851247350L;
    @Id
    @Column(name = "TX_COD_TIPO_CAIXA")
    private String id;

    @Column(name = "TX_DESCRICAO", nullable = false)
    private String description;
}
