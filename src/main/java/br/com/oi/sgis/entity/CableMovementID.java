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
public class CableMovementID implements Serializable {

    private static final long serialVersionUID = -2746580952496132995L;
    @Column(name = "MD_SEQ")
    private Long sequence;

    @ManyToOne
    @JoinColumn(name = "MD_COD_BARRAS", nullable = false)
    private Unity unity;


}
