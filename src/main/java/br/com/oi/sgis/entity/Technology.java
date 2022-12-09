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
@Table(name = "TECNOLOGIA")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Technology implements Serializable {

    private static final long serialVersionUID = 8901397824352951282L;
    @Id
    @Column(name = "TN_CODIGO")
    private String id;

    @Column(name = "TN_DESCRICAO", nullable = false)
    private String description;
}
