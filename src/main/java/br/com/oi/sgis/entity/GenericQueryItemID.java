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
public class GenericQueryItemID implements Serializable {

    private static final long serialVersionUID = 9065728548698439729L;

    @ManyToOne
    @JoinColumn(name = "CI_COD_CONSULTA", nullable = false)
    private GenericQuery genericQuery;

    @Column(name = "CI_SEQUENCE", nullable = false)
    private Long columnSequence;
}
