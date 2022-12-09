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
@Table(name = "APLICACAO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Application implements Serializable {

    private static final long serialVersionUID = 23109077983938302L;
    @Id
    @Column(name = "AP_COD_APLICACAO", nullable = false)
    private String id;

    @Column(name = "AP_DESCRICAO", nullable = false)
    private String description;


}
