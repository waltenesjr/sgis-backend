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
import java.util.List;

@Entity
@Table(name = "EMPRESAS")
@Data @AllArgsConstructor
@Builder @NoArgsConstructor
public class Company implements Serializable {

    private static final long serialVersionUID = 1055883656681225086L;

    @Id
    @Column(name = "E_CGC_CPF")
    private String id;

    @Column(name = "E_RAZAO", nullable = false)
    private String companyName;

    @Column(name = "E_NOME", nullable = false)
    private String tradeName;

    @Column(name = "E_INEST")
    private String stateRegistration;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "E_CLIENTE")
    private boolean client;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "E_PRESTADOR", nullable = false)
    private boolean provider;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "E_FABRICANTE", nullable = false)
    private boolean manufacturer;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "E_HOLDING", nullable = false)
    private boolean holding;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "E_CGC_CPF_VAL", nullable = false)
    private boolean validCgcCpf;

    @Column(name = "E_FLAG_CGC_CPF", nullable = false)
    private String cgcCpfFlag;

    @Column(name = "E_COD_EAN", nullable = false)
    private String eanCode;

    @Column(name = "E_SIGLA_SAP", nullable = false)
    private String sapAbbrev;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "E_ATIVO", nullable = false)
    private boolean active;

    @Column(name = "E_CNPJ_CPF", nullable = false)
    private String cnpjCpf;

    @ManyToOne
    @JoinColumn(name = "PF_COD_FABRICANTE")
    private Manufacturer manufacturerCode;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cgcCpf")
    private List<Address> addresses;
}
