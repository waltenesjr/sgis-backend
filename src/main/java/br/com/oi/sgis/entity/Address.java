package br.com.oi.sgis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ENDERECOS")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address implements Serializable {

    private static final long serialVersionUID = -231432528770943177L;
    @Id
    @Column(name = "EN_COD_ENDERECO")
    private String id;

    @ManyToOne
    @JoinColumn(name = "EN_CGC_CPF")
    private Company cgcCpf;

    @Column(name = "EN_DESCRICAO")
    private String description;

    @ManyToOne
    @JoinColumn(name = "EN_UF")
    private Uf uf;

    @Column(name = "EN_ENDERECO")
    private String addressDescription;

    @Column(name = "EN_BAIRRO")
    private String district;

    @Column(name = "EN_CIDADE")
    private String city;

    @Column(name = "EN_CEP")
    private String cep;

    @Column(name = "EN_TELEFONE")
    private String phone;

    @Column(name = "EN_FAX")
    private String fax;

    @Column(name = "EN_CONTATO")
    private String contact;
}
