package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "PTL_MODELO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Model implements Serializable {

    private static final long serialVersionUID = -9187777127229830764L;
    @EmbeddedId
    private ModelId id;

    @Column(name = "PM_DESC_MODELO")
    private String description;

    @Column(name = "PM_DATA_INSERCAO")
    private LocalDateTime inserctionDate;
}
