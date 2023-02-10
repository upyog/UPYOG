package org.ksmart.birth.ksmartbirthapplication.model.newbirth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.apache.kafka.common.protocol.types.Field;
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
    private String fileumbero;

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
}
