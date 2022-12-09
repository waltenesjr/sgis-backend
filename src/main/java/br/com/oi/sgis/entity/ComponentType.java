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
@Table(name = "TIPO_COMPONENTE")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComponentType implements Serializable {

    private static final long serialVersionUID = 8301359538638912846L;
    @Id
    @Column(name = "TC_COD_TIPO_COMP")
    private String id;

    @Column(name = "TC_DESCRICAO", nullable = false)
    private String description;
}
