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
@Table(name = "SITUACAO_REP")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepSituation implements Serializable {

    private static final long serialVersionUID = -3724708742285675746L;
    @Id
    @Column(name = "SR_COD_SIT_REP", nullable = false)
    private String id;

    @Column(name = "SR_DESCRICAO", nullable = false)
    private String description;


}
