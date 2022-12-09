package org.bel.birthdeath.crdeathregistry.web.models;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
     * Creates CrDeathAddressInfo model
     * Rakhi S IKM
     * on 28.11.2022
     */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrDeathRegistryAddressInfo {

    @JsonProperty("presentAddress")
    private CrDeathRegistryAddress presentAddress;

    @JsonProperty("permanentAddress")
    private CrDeathRegistryAddress permanentAddress;

    @JsonProperty("informantAddress")
    private CrDeathRegistryAddress informantAddress;

    @JsonProperty("deathplaceAddress")
    private CrDeathRegistryAddress deathplaceAddress;

    @JsonProperty("burialAddress")
    private CrDeathRegistryAddress burialAddress;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;   
    
    @Size(max = 64)
    @JsonProperty("parentdeathDtlId")
    private String parentdeathDtlId ; 
}
