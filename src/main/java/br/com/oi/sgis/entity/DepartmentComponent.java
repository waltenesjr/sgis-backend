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
@Table(name = "COMPONENTES_DEPTO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentComponent implements Serializable {

    private static final long serialVersionUID = -3231979903659998088L;
    @EmbeddedId
    private DepartmentComponentID id;

    @Column(name ="CD_VALOR")
    private BigDecimal value;

    @Column(name ="CD_QUANT")
    private Integer quantity;

    @Column(name ="CD_LOCACAO")
    private String location;
}
