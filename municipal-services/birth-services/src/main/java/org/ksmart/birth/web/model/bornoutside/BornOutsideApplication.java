package org.ksmart.birth.web.model.bornoutside;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ksmart.birth.birthcommon.model.demand.Demand;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.common.model.Document;
import org.ksmart.birth.web.model.InformatDetail;
import org.ksmart.birth.web.model.InitiatorDetail;
import org.ksmart.birth.web.model.ParentAddress;
import org.ksmart.birth.web.model.ParentsDetail;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BornOutsideApplication {
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
    @Size(max = 64)
    @JsonProperty("childPassportNo")
    private String childPassportNo;
    @Size(max = 64)
    @JsonProperty("childArrivalDate")
    private Long childArrivalDate;
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
    @Size(max = 1000)
    @JsonProperty("outsideBirthPlaceEn")
    private String outsideBirthPlaceEn;

    @Size(max = 1000)
    @JsonProperty("outsideBirthPlaceMl")
    private String outsideBirthPlaceMl;
    @Size(max = 1000)
    @JsonProperty("provinceEn")
    private String provinceEn;

    @Size(max = 1000)
    @JsonProperty("provinceMl")
    private String provinceMl;

    @Size(max = 1000)
    @JsonProperty("cityTownEn")
    private String cityTown;

    @Size(max = 1000)
    @JsonProperty("cityTownMl")
    private String cityTownMl;

    @Size(max = 1000)
    @JsonProperty("postCode")
    private String postCode;

    @Size(max = 64)
    @JsonProperty("country")
    private String country;
    @Size(max = 300)
    @JsonProperty("countryEn")
    private String countryEn;
    @Size(max = 300)
    @JsonProperty("countryMl")
    private String countryMl;
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
    @Size(max = 64)
    @JsonProperty("status")
    private String status;

    @Size(max = 64)
    @JsonProperty("applicationStatus")
    private String applicationStatus;

    @Size(max = 64)
    @JsonProperty("isWorkflow")
    private Boolean isWorkflow = true;

    @JsonProperty("assignee")
    private List<String> assignee;
    private String comment;
    @JsonProperty("wfDocuments")
    private List<Document> wfDocuments;
    @Size(max = 2500)
    @JsonProperty("remarks_en")
    private String remarksEn;
    @Size(max = 2500)
    @JsonProperty("remarks_ml")
    private String remarksMl;
    @Size(max = 20)
    @JsonProperty("am_pm")
    private String ampm;
    @JsonProperty("dateofreport")
    private Long dateOfReport;
    @Size(max = 64)
    @JsonProperty("birthPlaceUuid")
    private String birthPlaceUuid;
    @Size(max = 64)
    @JsonProperty("birthInitiatorUuid")
    private String birthInitiatorUuid;
    @Size(max = 64)
    @JsonProperty("birthStatisticsUuid")
    private String birthStatisticsUuid;
    @JsonProperty("isBornOutside")
    private Boolean isBornOutside;
    @JsonProperty("Demands")
    private List<Demand> demands;
    @JsonProperty("BornOutsideParentsDetails")
    private ParentsDetail parentsDetails;
    @JsonProperty("BornOutsideAddressBirthDetails")
    private ParentAddress parentAddress;
    @JsonProperty("BornOutsideStaticInfn")
    private BornOutsideStatInfo bornOutsideStaticInformant;
    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}
