package org.upyog.cdwm.web.models;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.upyog.cdwm.util.CNDServiceUtil;

import com.fasterxml.jackson.annotation.JsonFormat;

import digit.models.coremodels.AuditDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Valid
@Getter
@Setter
@NoArgsConstructor
public class CNDApplicationDetail {

    public enum ApplicationType{
        REQUEST_FOR_PICKUP, DEPOSIT_TO_THE_CENTER;
    }
    private String tenantId;

    private String applicationNumber;

    private String applicationId;

    private ApplicationType applicationType;

    private String typeOfConstruction;

    private String depositCentreDetails;
    
    private String description;
    
    private String location;
    
    private String wasteType;
    
    private String vehicleType;
    
    private Integer quantity;

    private String applicantDetailId;

    @JsonFormat(pattern = CNDServiceUtil.DATE_FORMAT)
    private LocalDate requestedPickupDate;

    //TODO: Convert to enum
    private String applicationStatus;

    AuditDetails auditDetails;

    private Object additionalDetails;

    private Long houseArea;

    @JsonFormat(pattern = CNDServiceUtil.DATE_FORMAT)
    private LocalDate constructionFromDate;

    @JsonFormat(pattern = CNDServiceUtil.DATE_FORMAT)
    private LocalDate constructionToDate;

    private String propertyType;

    private BigDecimal totalWasteQuantity;

    private Integer noOfTrips;

    private String vehicleId;

    private String vendorId;

    @JsonFormat(pattern = CNDServiceUtil.DATE_FORMAT)
    private LocalDate pickupDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime completedOn;

    private List<WasteTypeDetail> wasteTypeDetails;

    private FacilityCenterDetail facilityCenterDetail;

    private List<DocumentDetail> documentDetails;
    
    private Workflow workflow;




}
