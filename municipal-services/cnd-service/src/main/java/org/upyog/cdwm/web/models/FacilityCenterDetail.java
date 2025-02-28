package org.upyog.cdwm.web.models;

import digit.models.coremodels.AuditDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class FacilityCenterDetail {
	
	private String disposalId;
	 
    private String applicationId;

    private String vehicleId;

    private String vehicleDepotNo;

    private BigDecimal netWeight;

    private  BigDecimal grossWeight;

    private String dumpingStationName;

    private LocalDateTime disposalDate;

    private String disposalType;

    private String nameOfDisposalSite;

    private AuditDetails auditDetails;


}
