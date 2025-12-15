package org.egov.tobacco.web.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TobaccoLicenseRequest {
    private String establishmentName;
    private String establishmentVillage;
    private String establishmentTown;
    private String establishmentCity;
    private String establishmentBlock;
    private String establishmentDistrict;
    private String establishmentState;
    private String establishmentPinCode;
    private String establishmentTelephone;
    private String establishmentMobile;
    private String establishmentEmail;
    private String acknowledgmentNumber;

    private OwnerDetails owner;

    private String applicationType; 
    private String previousRegistrationNumber;

    private String paymentMode;
    private Double amount;
    private String paymentDetails;
    private String receiptNumber;
}
