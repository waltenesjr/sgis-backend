package br.com.oi.sgis.entity;

import br.com.oi.sgis.util.BooleanToStringConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "PESSOAL_TECNICO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TechnicalStaff implements Serializable {

    private static final long serialVersionUID = 7270839364570933578L;
    @Id
    @Column(name = "COD_MATRICULA_TEC")
    private String id;

    @Column(name = "NOME_TECNICO", nullable = false)
    private String technicianName;

    @ManyToOne
    @JoinColumn(name = "CGC_CPF_EMPR")
    private Parameter cgcCpfCompany;

    @Column(name = "NUM_TEL_BASE_TEC")
    private String techPhoneBase;

    @Column(name = "NUM_TEL_RESID_TEC")
    private String techPhoneResid;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "FLAG_ATIVO")
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COD_LOTACAO_TEC")
    private Department departmentCode;

    @Column(name = "NOME_GERENTE_TEC")
    private String managerName;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "FLAG_REPARO")
    private boolean repairFlag;

    @Column(name = "VALOR_HOMEM_HORA")
    private BigDecimal manHourValue;

    @Column(name = "EMAIL")
    private String email;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "techinician")
    private UserRegister userRegister;
}

