package org.ksmart.birth.web.model.correction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ksmart.birth.birthcommon.model.demand.Demand;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.common.model.Document;
import org.ksmart.birth.web.model.ParentAddress;

import javax.validation.constraints.Size;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CorrectionApplication {
    @Size(max = 64)
    @JsonProperty("id")
    private String id;
    @JsonProperty("dateofreport")
    private Long dateOfReport;
    @JsonProperty("childDOB")
    private Long dateOfBirth;
    @Size(max = 64)
    @JsonProperty("gender")
    private String gender;
    @Size(max = 64)
    @JsonProperty("childAadharNo")
    private String aadharNo;
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
    @JsonProperty("birthPlace")
    private String placeofBirthId;
    @Size(max = 64)
    @JsonProperty("hospitalCode")
    private String hospitalId;
    @Size(max = 64)
    @JsonProperty("hospitalName")
    private String hospitalName;
    @Size(max = 64)
    @JsonProperty("hospitalNameMl")
    private String hospitalNameMl;
    @Size(max = 64)
    @JsonProperty("institutionTypeCode")
    private String institutionTypeId;
    @Size(max = 64)
    @JsonProperty("institution")
    private String institution;
    @Size(max = 64)
    @JsonProperty("institutionNameCode")
    private String institutionNameCode;
    @Size(max = 64)
    @JsonProperty("institutionId")
    private String institutionId;
    @Size(max = 64)
    @JsonProperty("institutionIdMl")
    private String institutionIdMl;
    @Size(max = 64)
    @JsonProperty("wardNo")
    private String wardId;
    @Size(max = 64)
    @JsonProperty("wardNameEn")
    private String wardNameEn;
    @Size(max = 64)
    @JsonProperty("wardNameMl")
    private String wardNameMl;
    @Size(max = 64)
    @JsonProperty("wardNumber")
    private String wardNumber;
    @Size(max = 2000)
    @JsonProperty("adrsHouseNameEn")
    private String adrsHouseNameEn;
    @Size(max = 2000)
    @JsonProperty("adrsHouseNameMl")
    private String adrsHouseNameMl;
    @Size(max = 2000)
    @JsonProperty("adrsLocalityNameEn")
    private String adrsLocalityNameEn;
    @Size(max = 2000)
    @JsonProperty("adrsLocalityNameMl")
    private String adrsLocalityNameMl;
    @Size(max = 2000)
    @JsonProperty("adrsStreetNameEn")
    private String adrsStreetNameEn;
    @Size(max = 2000)
    @JsonProperty("adrsStreetNameMl")
    private String adrsStreetNameMl;
    @Size(max = 2000)
    @JsonProperty("adrsPostOffice")
    private String adrsPostOffice;
    @Size(max = 10)
    @JsonProperty("adrsPincode")
    private String adrsPincode;
    @Size(max = 64)
    @JsonProperty("vehicleType")
    private String vehicleTypeid;
    @Size(max = 64)
    @JsonProperty("vehicleHaltPlace")
    private String vehicleHaltplace;
    @Size(max = 2000)
    @JsonProperty("vehicleHaltPlaceMl")
    private String vehicleHaltPlaceMl;
    @Size(max = 64)
    @JsonProperty("vehicleRegistrationNo")
    private String vehicleRegistrationNo;
    @Size(max = 64)
    @JsonProperty("vehicleFromEn")
    private String vehicleFromEn;
    @Size(max = 1000)
    @JsonProperty("vehicleToEn")
    private String vehicleToEn;
    @Size(max = 1000)
    @JsonProperty("vehicleFromMl")
    private String vehicleFromMl;
    @Size(max = 1000)
    @JsonProperty("vehicleToMl")
    private String vehicleToMl;
    @Size(max = 1000)
    @JsonProperty("setadmittedHospitalEn")
    private String setadmittedHospitalEn;
    @Size(max = 1000)
    @JsonProperty("vehicleDesDetailsEn")
    private String vehicleDesDetailsEn;
    @Size(max = 64)
    @JsonProperty("publicPlaceType")
    private String publicPlaceType;
    @Size(max = 1000)
    @JsonProperty("localityNameEn")
    private String localityNameEn;
    @Size(max = 1000)
    @JsonProperty("localityNameMl")
    private String localityNameMl;
    @Size(max = 1000)
    @JsonProperty("streetNameEn")
    private String streetNameEn;
    @Size(max = 1000)
    @JsonProperty("streetNameMl")
    private String streetNameMl;
    @Size(max = 1000)
    @JsonProperty("publicPlaceDecpEn")
    private String publicPlaceDecpEn;
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
    @JsonProperty("villageid")
    private String villageid;
    @Size(max = 64)
    @JsonProperty("talukid")
    private String talukid;
    @Size(max = 64)
    @JsonProperty("districtid")
    private String districtid;
    @Size(max = 64)
    @JsonProperty("countryid")
    private String countryid;
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
    @JsonProperty("fileNumber")
    private String fileNumber;
    @JsonProperty("fileDate")
    private Long fileDate;
    @JsonProperty("fileStatus")
    private String fileStatus;
    @Size(max = 64)
    @JsonProperty("applicationNumber")
    private String applicationNo;
    @Size(max = 64)
    @JsonProperty("registrationNo")
    private String registrationNo;
    @JsonProperty("registrationDate")
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
    @JsonProperty("applicationStatus")
    private String applicationStatus;
    @Size(max = 64)
    @JsonProperty("birthPlaceUuid")
    private String birthPlaceUuid;

// Mother Details
    @Size(max = 64)
    @JsonProperty("motherUuid")
    private String motherUuid;
    @Size(max = 2000)
    @JsonProperty("motherFirstNameEn")
    private String motherFirstNameEn;
    @Size(max = 2000)
    @JsonProperty("motherFirstNameMl")
    private String motherFirstNameMl;

    @Size(max = 64)
    @JsonProperty("motherAadhar")
    private String motherAadhar;

//Father Details

    @Size(max = 64)
    @JsonProperty("fatherUuid")
    private String fatherUuid;
    @Size(max = 64)
    @JsonProperty("fatherAadhar")
    private String fatherAadharno;
    @Size(max = 2000)
    @JsonProperty("fatherFirstNameEn")
    private String fatherFirstNameEn;
    @Size(max = 2000)
    @JsonProperty("fatherFirstNameMl")
    private String fatherFirstNameMl;


    @JsonProperty("AddressBirthDetails")
    private ParentAddress parentAddress;
    @JsonProperty("Demands")
    private List<Demand> demands;
    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}

