package br.com.oi.sgis.entity;

import br.com.oi.sgis.util.BooleanToStringConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "SHCAD")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegister implements Serializable {

    private static final long serialVersionUID = 8145805859039594287L;
    @Id
    @Column(name = "SHC_MATRICULA")
    private String id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "SHC_MATRICULA")
    private TechnicalStaff techinician;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "SHNIVEIS_FUNC", joinColumns = @JoinColumn(name = "SHV_MATRICULA"), inverseJoinColumns = @JoinColumn(name = "SHV_NIVEL"))
    private Set<Level> levels = new HashSet<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHC_LOTACAO")
    private Department department;

    @Column(name = "SHC_SENHA")
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHC_AUTORIZADOR")
    private Department authorizing;

    @Column(name = "SHC_LOGIN_NDS")
    private String ndsLogin;

    @Convert(converter= BooleanToStringConverter.class)
    @Column(name = "SHC_FLAG_ATIVADO")
    private boolean active;


}
