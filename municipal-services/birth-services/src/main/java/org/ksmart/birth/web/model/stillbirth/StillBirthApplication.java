package org.ksmart.birth.web.model.stillbirth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.common.model.Document;
import org.ksmart.birth.web.model.InformatDetail;
import org.ksmart.birth.web.model.InitiatorDetail;
import org.ksmart.birth.web.model.ParentAddress;
import org.ksmart.birth.web.model.ParentsDetail;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StillBirthApplication {
    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @JsonProperty("dateofreport")
    private Long dateOfReport;

    @JsonProperty("childDOB")
    private Long dateOfBirth;

    @JsonProperty("birthDateTime")
    private String timeOfBirth;

    @JsonProperty("birthTime")
    private Long timeBirth;

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
    @JsonProperty("birthPlace")
    private String placeofBirthId;

    @Size(max = 64)
    @JsonProperty("birthPlaceEn")
    private String placeofBirthIdEn;

    @Size(max = 64)
    @JsonProperty("birthPlaceMl")
    private String placeofBirthIdMl;

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
    @JsonProperty("institutionTypeEn")
    private String institutionTypeEn;
    @Size(max = 64)
    @JsonProperty("institutionTypeMl")
    private String institutionTypeMl;
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

    @Size(max = 128)
    @JsonProperty("adrsPostOffice")
    private String adrsPostOffice;

    @Size(max = 300)
    @JsonProperty("adrsPostOfficeEn")
    private String adrsPostOfficeEn;

    @Size(max = 300)
    @JsonProperty("adrsPostOfficeMl")
    private String adrsPostOfficeMl;

    @Size(max = 10)
    @JsonProperty("adrsPincode")
    private String adrsPincode;
    @Size(max = 64)
    @JsonProperty("vehicleType")
    private String vehicleTypeid;
    @Size(max = 300)
    @JsonProperty("vehicleTypeEn")
    private String vehicleTypeidEn;
    @Size(max = 300)
    @JsonProperty("vehicleTypeMl")
    private String vehicleTypeidMl;
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
    @Size(max = 64)
    @JsonProperty("setadmittedHospitalEn")
    private String setadmittedHospitalEn;
    @Size(max = 64)
    @JsonProperty("vehicleDesDetailsEn")
    private String vehicleDesDetailsEn;
    @Size(max = 64)
    @JsonProperty("publicPlaceType")
    private String publicPlaceType;
    @Size(max = 300)
    @JsonProperty("publicPlaceTypeEn")
    private String publicPlaceTypeEn;
    @Size(max = 300)
    @JsonProperty("publicPlaceTypeMl")
    private String publicPlaceTypeMl;
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
    @JsonProperty("birthWeight")
    private double birthWeight;
    @Size(max = 64)
    @JsonProperty("pregnancyDuration")
    private Integer pregnancyDuration;
    @Size(max = 64)
    @JsonProperty("medicalAttensionSub")
    private String medicalAttensionSub;

    @Size(max = 300)
    @JsonProperty("medicalAttensionSubEn")
    private String medicalAttensionSubEn;

    @Size(max = 300)
    @JsonProperty("medicalAttensionSubMl")
    private String medicalAttensionSubMl;

    @Size(max = 64)
    @JsonProperty("deliveryMethods")
    private String deliveryMethods;
    @Size(max = 300)
    @JsonProperty("deliveryMethodsEn")
    private String deliveryMethodsEn;
    @Size(max = 300)
    @JsonProperty("deliveryMethodsMl")
    private String deliveryMethodsMl;
    @Size(max = 64)
    @JsonProperty("esign_user_code")
    private String esignUserCode;
    @Size(max = 64)
    @JsonProperty("esign_user_desig_code")
    private String esignUserDesigCode;
    @Size(max = 64)
    @JsonProperty("tenantid")
    private String tenantId;
    @Size(max = 300)
    @JsonProperty("tenantidEn")
    private String tenantIdEn;
    @Size(max = 300)
    @JsonProperty("tenantidMl")
    private String tenantIdMl;
    @Size(max = 64)
    @JsonProperty("villageid")
    private String villageid;

    @Size(max = 300)
    @JsonProperty("villageidEn")
    private String villageIdEn;

    @Size(max = 300)
    @JsonProperty("villageidMl")
    private String villageIdMl;

    @Size(max = 64)
    @JsonProperty("talukid")
    private String talukid;

    @Size(max = 300)
    @JsonProperty("talukidEn")
    private String talukIdEn;

    @Size(max = 300)
    @JsonProperty("talukidMl")
    private String talukIdMl;

    @Size(max = 64)
    @JsonProperty("districtid")
    private String districtid;

    @Size(max = 64)
    @JsonProperty("countryid")
    private String countryid;


    @Size(max = 300)
    @JsonProperty("countryidEn")
    private String countryidEn;
    @Size(max = 300)
    @JsonProperty("countryidMl")
    private String countryidMl;

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

    @JsonProperty("isWorkflow")
    private Boolean isWorkflow = true;

    @JsonProperty("wfDocuments")
    private List<Document> wfDocuments;
    @Size(max = 64)
    @JsonProperty("applicationStatus")
    private String applicationStatus;
    @Size(max = 2500)
    @JsonProperty("remarks_en")
    private String remarksEn;
    @Size(max = 2500)
    @JsonProperty("remarks_ml")
    private String remarksMl;
    @Size(max = 20)
    @JsonProperty("am_pm")
    private String ampm;
    @Size(max = 64)
    @JsonProperty("birthPlaceUuid")
    private String birthPlaceUuid;
    @Size(max = 64)
    @JsonProperty("birthInitiatorUuid")
    private String birthInitiatorUuid;
    @Size(max = 64)
    @JsonProperty("birthStatisticsUuid")
    private String birthStatisticsUuid;
    @NotNull
    @Size(max = 64)
    @JsonProperty("causeFoetalDeath")
    private String causeFoetalDeath;

    @Size(max = 300)
    @JsonProperty("causeFoetalDeathEn")
    private String causeFoetalDeathEn;

    @Size(max = 300)
    @JsonProperty("causeFoetalDeathMl")
    private String causeFoetalDeathMl;

    @JsonProperty("StillBirthParentsDetails")
    private ParentsDetail parentsDetails;

    @JsonProperty("StillBirthInitiatorDetails")
    private InitiatorDetail initiatorDetails;

    @JsonProperty("AddressBirthDetails")
    private ParentAddress parentAddress;
    @JsonProperty("StillBirthInformarHosInstDetails")
    private InformatDetail informatDetail;

    @JsonProperty("isStill")
    private Boolean isStill;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}
