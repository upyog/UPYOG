package digit.web.models;

import java.util.List;

import org.apache.kafka.common.protocol.types.Field.Str;
import org.egov.common.contract.models.Address;
import org.egov.common.contract.models.AuditDetails;
import org.egov.common.contract.models.Workflow;
import org.egov.common.contract.request.User;

import com.fasterxml.jackson.annotation.JsonProperty;

import digit.bmc.model.AadharUser;
import digit.bmc.model.Bank;
import digit.bmc.model.BankAccount;
import digit.bmc.model.BankBranch;
import digit.bmc.model.BmcRegistrationApplication;
import digit.bmc.model.Caste;
import digit.bmc.model.Courses;
import digit.bmc.model.Divyang;
import digit.bmc.model.Machines;
import digit.bmc.model.Schemes;
import digit.bmc.model.Sector;
import digit.bmc.model.UserOtherDetails;
import digit.bmc.model.UserSchemeApplication;
import digit.web.models.scheme.SchemeTypeDTO;
import digit.web.models.scheme.UpdateSchemeDataDTO;
import digit.web.models.user.DocumentDetails;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a scheme application entity.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchemeApplication {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("applicationNumber")
    private String applicationNumber;

    @JsonProperty("numberOfMachines")
    private Long numberOfMachines;
    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("optedId")
    private Long optedId;

    @JsonProperty("applicationStatus")
    private Boolean applicationStatus;

    @JsonProperty("verificationStatus")
    private Boolean verificationStatus;

    @JsonProperty("firstApprovalStatus")
    private Boolean firstApprovalStatus;

    @JsonProperty("randomSelection")
    private Boolean randomSelection;

    @JsonProperty("finalApproval")
    private Boolean finalApproval;

    @JsonProperty("submitted")
    private Boolean submitted;

    @JsonProperty("modifiedOn")
    private Long modifiedOn;

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("modifiedBy")
    private String modifiedBy;

    @JsonProperty("address")
    private Address address;
   
    @JsonProperty("bank")
    private Bank bank;
    @JsonProperty("bank_Account")
    private BankAccount bankAccount;
    @JsonProperty("bank_Branch")
    private BankBranch bankBranch;
    @JsonProperty("aadharUser")
    private AadharUser aadharUser;
    @JsonProperty("sector")
    private Sector sector;
    
    @JsonProperty("caste")
    private Caste caste;
    @JsonProperty("userOtherDetails")
    private UserOtherDetails userOtherDetails;
    @JsonProperty("divyang")
    private Divyang divyang;
    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
    @JsonProperty("UserSchemeApplication")
    private UserSchemeApplication userSchemeApplication;
    @JsonProperty("bmcRegistrationApplication")
    private BmcRegistrationApplication bmcRegistrationApplication;
    @JsonProperty("user")
    private User user;

    @JsonProperty ("wards")
    private Ward wards;
    @JsonProperty("qualification")
    private Qualification qualification;
    @JsonProperty("egBmcUserQualification")
    private EgBmcUserQualification egBmcUserQualification;
    @JsonProperty("egBoundary")
    private EgBoundary egBoundary;    
    @Valid
    @JsonProperty("workflow")
    private Workflow workflow = null;

  
    @JsonProperty("ProcessInstance")
    private ProcessInstance processInstance;

    @JsonProperty("machines")
    private Machines machines;
  
    @JsonProperty("course")
    private Courses course;
  
    @JsonProperty("scheme")
    private Schemes schemes;

    @JsonProperty("schemeType")
    private SchemeTypeDTO schemeType;

    @JsonProperty("updateSchemeData")
    private UpdateSchemeDataDTO updateSchemeData;

 
}
