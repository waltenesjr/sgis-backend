package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "SGE_UF")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Uf implements Serializable {

    private static final long serialVersionUID = 6743989863467102709L;
    @Id
    @Column(name = "UF")
	private String id;

    @Column(name = "NOME_UF ", nullable = false)
    @NotBlank(message = "É necessário informar o nome")
    private String name;
}
