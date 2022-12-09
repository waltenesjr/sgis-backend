package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "FAS_COD_BARRA")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BarcodeFAS implements Serializable {


    private static final long serialVersionUID = -4669884204881397886L;
    @Id
    @Column(name = "FACB_NUM_FAS", nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "UN_COD_BARRAS", nullable = false)
    private Unity unity;
}
