package br.com.oi.sgis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstallationTransferDTO {

    private CompanyDTO providerResponsible;
    private CompanyDTO client;
    private StationDTO stationInst;
    private CentralDTO centralDTO;
    private String installationRack;
    private TechnicalStaffDTO installationTechnician;
    private String baNumber;
    private String tpNumber;
    private String latitude;
    private String longitude;
    private AddressDTO address;
}
