package br.com.oi.sgis.dto;

import br.com.oi.sgis.annotations.NotBlankOrNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnityDTO {

    @NotBlank(message = "O código de barras não deve ser nulo")
    @Size(max = 40,message = "O código de barras deve possuir no máximo 40 caracteres")
    private String id;

    @NotBlank(message = "O número de série não deve ser nulo")
    @Size(max = 30,message = "O número de série deve possuir no máximo 30 caracteres")
    private String serieNumber;

    @NotBlankOrNull(message = "O número item não deve ser informado em branco")
    @Size(max = 10,message = "O número item deve possuir no máximo 10 caracteres")
    private String orderNumber;

    @NotBlankOrNull(message = "O pedido item não deve ser informado em branco")
    @Size(max = 5,message = "O pedido item deve possuir no máximo 5 caracteres")
    private String orderItem;

    @NotBlankOrNull(message = "O número imobilizado não deve ser informado em branco")
    @Size(max = 12,message = "O número imobilizado deve possuir no máximo 12 caracteres")
    private String fixedNumber;

    @NotBlankOrNull(message = "O subnúmero imobilizado não deve ser informado em branco")
    @Size(max = 4,message = "O subnúmero imobilizado deve possuir no máximo 4 caracteres")
    private String fixedSubnumber;

    @NotBlankOrNull(message = "O item reserva não deve ser informado em branco")
    @Size(max = 4,message = "O item reserva deve possuir no máximo 4 caracteres")
    private String reservationItem;

    @NotBlankOrNull(message = "O número reserva não deve ser informado em branco")
    @Size(max = 9,message = "O número reserva deve possuir no máximo 9 caracteres")
    private String reservationNumber;

    @NotNull(message = "O responsável não deve ser nulo")
    private DepartmentDTO responsible;

    @NotNull(message = "O depósito não deve ser nulo")
    private DepartmentDTO deposit;

    @NotNull(message = "O código de unidade não deve ser nulo")
    private AreaEquipamentDTO unityCode;

    @NotNull(message = "O técnico não deve ser nulo")
    private TechnicalStaffDTO technician;

    private SituationDTO situationCode;

    @Size(max = 2,message = "A UF deve possuir no máximo 2 caracteres")
    private String uf;

    @Size(max = 2,message = "A UF de origem deve possuir no máximo 2 caracteres")
    private String originUf;

    private DepartmentDTO destination;

    private StationDTO station;

    private LocalDateTime situationDateChange;

    @Digits(integer = 7, fraction = 2, message = "O valor deve possuir o formato 9999999.99, com no máximo 7 casas inteiras e 2 casas decimais")
    private BigDecimal value;

    @Size(max = 3,message = "O motivo de cadastro deve possuir no máximo 3 caracteres")
    private String registerReason;

    @Size(max = 3,message = "O motivo de instalação deve possuir no máximo 3 caracteres")
    private String instalationReason;

    @Size(max = 10,message = "A versão deve possuir no máximo 10 caracteres")
    private String version;

    @Size(max = 12,message = "O tombamento deve possuir no máximo 12 caracteres")
    private String tipping;

    private FiscalDocumentDTO fiscalDocument;

    private BoxDTO box;

    private String accountantCompany;

    private String activeClass;

    private LocalDateTime deadline;

    private LocalDateTime warrantyDate;

    private String sapStatus;

    @Size(max = 250,message = "A observação deve possuir no máximo 250 caracteres")
    private String observation;

    @Size(max = 20,message = "A Localização deve possuir no máximo 20 caracteres")
    private String location;

    private String boNumber;

    private List<ElectricalPropUnityDTO> electricalProperties;

    private String descriptionProcedure;

    private String sapMesage;

    private String sapStatusDescription;

    private Integer provider;

    private Integer providerAccountant;

    public String getId(){
        return replaceAllSpaces(this.id);
    }

    public String getSerieNumber(){
        return replaceAllSpaces(this.serieNumber);
    }

    private String replaceAllSpaces(String property){
        return property != null ? property.replaceAll("\\s+","") : null;
    }

    public boolean getSapStatus(){
        return !(this.sapStatus ==null || !this.sapStatus.equals("0"));
    }



}
