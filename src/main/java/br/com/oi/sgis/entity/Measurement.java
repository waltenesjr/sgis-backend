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
@Table(name = "UN_MEDIDA")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Measurement implements Serializable {

    private static final long serialVersionUID = -397703466712487689L;
    @Id
    @Column(name = "UM_COD_UN_MED", nullable = false)
    private String id;

    @Column(name = "UM_DESCRICAO")
    private String description;

}
