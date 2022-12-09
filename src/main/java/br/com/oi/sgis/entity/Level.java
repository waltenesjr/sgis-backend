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
@Table(name = "SHNIVEL")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Level implements Serializable {

    private static final long serialVersionUID = -8936869862859062675L;
    @Id
    @Column(name = "SHN_CODIGO", nullable = false)
    private String id;

    @Column(name = "SHN_DESCRICAO", nullable = false)
    private String description;

    @Column(name = "SHN_NIVEL", nullable = false)
    private int lvl;

}
