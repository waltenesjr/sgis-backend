package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "DOLAR")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Dollar {
    @Id
    @Column(name = "DL_DATA", nullable = false)
    private LocalDateTime date;

    @Column(name = "DL_VALOR", nullable = false)
    private BigDecimal value;
}
