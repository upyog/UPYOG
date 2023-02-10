package org.ksmart.birth.ksmartbirthapplication.model.newbirth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.common.model.Document;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KsmartBirthAppliactionDetail {
    @Size(max = 64)
    @JsonProperty("id")
    private String id;
    @JsonProperty("childDOB")
    private Long dateOfBirth;
    @JsonProperty("birthDateTime")
    private Long timeOfBirth;
    @Size(max = 64)
    @JsonProperty("gender")
    private String gender;
    @Size(max = 64)
    @JsonProperty("childAadharNo")
    private String aadharNo;

    @JsonProperty("isChildName")
    private Boolean isChildName;
    @Size(max = 64)
    @JsonProperty("childFirstNameEn")
    private String firstNameEn;
    @Size(max = 64)
    @JsonProperty("childMiddleNameEn")
    private String middleNameEn;
    @Size(max = 64)
    @JsonProperty("childLastNameEn")
    private String lastNameEn;
    @Size(max = 64)
    @JsonProperty("childFirstNameMl")
    private String firstNameMl;
    @Size(max = 64)
    @JsonProperty("childMiddleNameMl")
    private String middleNameMl;
    @Size(max = 64)
    @JsonProperty("childLastNameMl")
    private String lastNameMl;

    @Size(max = 64)
    @JsonProperty("public_place_id")
    private String placeofBirthId;
    @Size(max = 64)
    @JsonProperty("hospitalid")
    private String hospitalId;
    @Size(max = 64)
    @JsonProperty("institution_type_id")
    private String institutionTypeId;
    @Size(max = 64)
    @JsonProperty("institution")
    private String institution;

    @Size(max = 64)
    @JsonProperty("institutionNameCode")
    private String institutionNameCode;
    @Size(max = 64)
    @JsonProperty("institutionId")
    private String institution_id;
    @Size(max = 64)
    @JsonProperty("institutionIdMl")
    private String institutionIdMl;
    @Size(max = 64)
    @JsonProperty("wardId")
    private String ward_id;
    @Size(max = 64)
    @JsonProperty("wardNameEn")
    private String wardNameEn;
    @Size(max = 64)
    @JsonProperty("wardNameMl")
    private String wardNameMl;
    @Size(max = 64)
    @JsonProperty("wardNumber")
    private String wardNumber;
    @Size(max = 64)
    @JsonProperty("ho_housename_en")
    private String adrsHouseNameEn;
    @Size(max = 64)
    @JsonProperty("ho_housename_ml")
    private String adrsHouseNameMl;
    @Size(max = 64)
    @JsonProperty("ho_locality_en")
    private String adrsLocalityNameEn;
    @Size(max = 64)
    @JsonProperty("ho_locality_ml")
    private String adrsLocalityNameMl;
    @Size(max = 64)
    @JsonProperty("street_locality_area_en")
    private String adrsStreetNameEn;
    @Size(max = 64)
    @JsonProperty("street_locality_area_ml")
    private String adrsStreetNameMl;
    @Size(max = 64)
    @JsonProperty("ho_poid")
    private String adrsPostOffice;
    @Size(max = 64)
    @JsonProperty("ho_pinno")
    private String adrsPincode;

    @Size(max = 64)
    @JsonProperty("vehicletypeid")
    private String vehicleTypeid;
    @Size(max = 2000)
    @JsonProperty("vehicle_haltplace")
    private String vehicleHaltplace;
    @Size(max = 2000)
    @JsonProperty("vehicleHaltPlaceMl")
    private String vehicleHaltPlaceMl;
    @Size(max = 64)
    @JsonProperty("vehicle_registration_no")
    private String vehicleRegistrationNo;
    @Size(max = 1000)
    @JsonProperty("vehicle_from_en")
    private String vehicleFromEn;
    @Size(max = 1000)
    @JsonProperty("vehicle_to_en")
    private String vehicleToEn;
    @Size(max = 1000)
    @JsonProperty("vehicle_from_ml")
    private String vehicleFromMl;
    @Size(max = 1000)
    @JsonProperty("vehicle_to_ml")
    private String vehicleToMl;
    @Size(max = 64)
    @JsonProperty("setadmittedHospitalEn")
    private String setadmittedHospitalEn;
    @Size(max = 64)
    @JsonProperty("vehicleDesDetailsEn")
    private String vehicleDesDetailsEn;
    @Size(max = 64)
    @JsonProperty("publicPlaceType")
    private String publicPlaceType;
    @Size(max = 64)
    @JsonProperty("localityNameEn")
    private String localityNameEn;
    @Size(max = 64)
    @JsonProperty("localityNameMl")
    private String localityNameMl;
    @Size(max = 64)
    @JsonProperty("streetNameEn")
    private String streetNameEn;
    @Size(max = 64)
    @JsonProperty("streetNameMl")
    private String streetNameMl;
    @Size(max = 64)
    @JsonProperty("publicPlaceDecpEn")
    private String publicPlaceDecpEn;
    @Size(max = 64)
    @JsonProperty("birthWeight")
    private String weight_of_child;
    @Size(max = 64)
    @JsonProperty("pregnancyDuration")
    private String duration_of_pregnancy_in_week;
    @Size(max = 64)
    @JsonProperty("medicalAttensionSub")
    private String medicalAttensionSub;
    @Size(max = 64)
    @JsonProperty("deliveryMethods")
    private String delivery_method;
    @Size(max = 64)
    @JsonProperty("esign_user_code")
    private String esignUserCode;

    @Size(max = 64)
    @JsonProperty("esign_user_desig_code")
    private String esignUserDesigCode;
    @Size(max = 64)
    @JsonProperty("tenantid")
    private String tenantId;

    @Size(max = 64)
    @JsonProperty("applicationtype")
    private String applicationType;

    @Size(max = 64)
    @JsonProperty("businessservice")
    private String businessService;

    @Size(max = 64)
    @JsonProperty("workflowcode")
    private String workFlowCode;

    @Size(max = 64)
    @JsonProperty("filenumber")
    private String fileNumber;

    @JsonProperty("file_date")
    private Long fileDate;

    @Size(max = 64)
    @JsonProperty("applicationnumber")
    private String applicationNo;

    @Size(max = 64)
    @JsonProperty("registrationno")
    private String registrationNo;

    @JsonProperty("registration_date")
    private Long registrationDate;

    @Size(max = 64)
    @JsonProperty("action")
    private String action;

    @JsonProperty("assignee")
    private List<String> assignee;

    private String comment;


    @JsonProperty("wfDocuments")
    private List<Document> wfDocuments;

    @Size(max = 64)
    @JsonProperty("status")
    private String status;

    @Size(max = 64)
    @JsonProperty("birthUuid")
    private String birthUuid;

    @Size(max = 64)
    @JsonProperty("birthStatisticsUuid")
    private String birthStatisticsUuid;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}
