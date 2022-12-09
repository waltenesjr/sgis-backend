package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelId implements Serializable {
    private static final long serialVersionUID = -3835514145798491057L;

    @Column(name = "PM_COD_MODELO", nullable = false)
    private Long modelCod;

    @ManyToOne
    @JoinColumn(name = "PM_COD_FABRICANTE", nullable = false)
    private Manufacturer manufacturerCod;

}
