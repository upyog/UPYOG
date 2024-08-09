package digit.web.models;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.egov.common.contract.models.Address;
import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import digit.bmc.model.UserSchemeApplication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchemeApplicationResponse {

   @JsonProperty("ResponseInfo")
   private ResponseInfo responseInfo;

   @JsonProperty("SchemeApplications")
   private List<SchemeApplication> schemeApplications;

   private List<UserSchemeApplication> randomizedCitizens;


   private String aadharRef;

   private String uuid;

   private String aadharfatherName;

   private String aadharname;

   private Date aadhardob;

   private String aadharmobile;

   private Date createdOn;

   private Date modifiedOn;

   private Integer createdBy;

   private String address1;

   private String address2;

   private String location;

   private String ward;

   private String city;

   private String district;

   private String pincode;

   private Integer optedId;

   private String applicationStatus;

   private String verificationStatus;

   private String firstApprovalStatus;

   private Boolean randomSelection;

   private Boolean finalApproval;

   private Boolean submitted;

   private String code;

   private String name;

   private String narration;

   private Boolean isActive;

   private String type;

   private Timestamp createdDate;

   private Timestamp lastModifiedDate;
   private Integer modifiedby;

   private Long branchId;

   private String accountNumber;

   private String accountType;

   private String payTo;

   private String branchcode;

   private String branchname;

   private String branchaddress1;

   private String branchaddress2;

   private String branchcity;

   private String branchstate;

   private String branchpin;

   private String branchphone;

   private String branchfax;
   private Long bankid;

   private String contactperson;

   private String micr;

   private String tenantId;

   private String applicationNumber;

   private String applicantName;

   private String fatherName;

   private String mobileNumber;

   private String emailId;

   private String aadharNumber;

   private Address address;

   private String courseName;

   private String description;

   private String duration;

   private Long startDt;

   private Long endDt;

   private Integer typeId;

   private String url;

   private String institute;

   private String imgUrl;

   private String instituteAddress;

   private Double amount;

   private Integer schemeId;

   private Integer courseId;

   private Double grantAmount;

   private String sector;
   private Long userId;

   private Integer religionId;

   private String divyangCardId;

   private Double divyangPercent;

   private String transgenderId;

   private String rationCardCategory;

   private String educationLevel;

   private String udid;

   private Long lastModifiedBy;

   private Long version;

   private Long chequeFormatId;

}
