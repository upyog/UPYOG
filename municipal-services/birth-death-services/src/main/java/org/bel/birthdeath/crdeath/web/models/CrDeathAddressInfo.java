package org.bel.birthdeath.crdeath.web.models;

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
     * 
     */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrDeathAddressInfo {

    @JsonProperty("presentAddress")
    private CrDeathAddress presentAddress;

    @JsonProperty("permanentAddress")
    private CrDeathAddress permanentAddress;

    @JsonProperty("informantAddress")
    private CrDeathAddress informantAddress;

    @JsonProperty("deathplaceAddress")
    private CrDeathAddress deathplaceAddress;

    @JsonProperty("burialAddress")
    private CrDeathAddress burialAddress;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;   
    
    @Size(max = 64)
    @JsonProperty("parentdeathDtlId")
    private String parentdeathDtlId ; 
}
