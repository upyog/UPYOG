package org.ksmart.birth.web.model.correction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ksmart.birth.birthcommon.model.demand.Demand;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.common.model.Document;
import org.ksmart.birth.web.model.DocumentDetails;
import org.ksmart.birth.web.model.ParentAddress;
import org.ksmart.birth.web.model.ParentsDetail;

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

    @Size(max = 64)
    @JsonProperty("registerid")
    private String registerId;

    private Long dateOfReport;

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
    @JsonProperty("fatherUuid")
    private String fatherUuid;
    @Size(max = 64)
    @JsonProperty("fatherAadhar")
    private String fatherAadhar;
    @Size(max = 2000)
    @JsonProperty("fatherFirstNameEn")
    private String fatherFirstNameEn;
    @Size(max = 2000)
    @JsonProperty("fatherFirstNameMl")
    private String fatherFirstNameMl;

    @Size(max = 64)
    @JsonProperty("motherUuid")
    private String motherUuid;

    @Size(max = 64)
    @JsonProperty("birthPlaceUuid")
    private String birthPlaceUuid;

    @Size(max = 64)
    @JsonProperty("motherAadhar")
    private String motherAadhar;
    @Size(max = 2000)
    @JsonProperty("motherFirstNameEn")
    private String motherfirstNameEn;
    @Size(max = 2000)
    @JsonProperty("motherFirstNameMl")
    private String motherfirstNameMl;

    @JsonProperty("CorrectionField")
    private List<CorrectionField> correctionField;

    @JsonProperty("CorrectionAddress")
    private CorrectionAddress correctionAddress;

    @JsonProperty("CorrectionParentDetails")
    private ParentsDetail correctionParentDetails;

    @JsonProperty("Demands")
    private List<Demand> demands;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}

