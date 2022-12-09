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
import java.time.LocalDateTime;

@Entity
@Table(name = "PTL_FABRICANTE")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Manufacturer implements Serializable {

    private static final long serialVersionUID = 8120579800808238732L;
    @Id
    @Column(name = "PF_COD_FABRICANTE")
    private String id;

    @Column(name = "PF_DESC_FABRICANTE")
    private String description;

    @Column(name = "PF_DATA_INSERCAO")
    private LocalDateTime insertionDate;

}
