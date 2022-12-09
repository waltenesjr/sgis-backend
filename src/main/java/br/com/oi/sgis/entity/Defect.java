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
@Table(name = "SGE_DESCR_DEFEITO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Defect implements Serializable {


    private static final long serialVersionUID = -3596308777598735489L;
    @Id
    @Column(name = "COD_DEFEITO", nullable = false)
    private String id;

    @Column(name = "DESCRICAO", nullable = false)
    private String description;
}
