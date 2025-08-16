
package org.egov.vendor.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "eg_Vendor_Additional_Details")
public class VendorAdditionalDetails {

    @Id
    @Column(name = "vendor_additional_details_id")
    private String vendorAdditionalDetailsId;

    @Column(name = "vendor_id", length = 64)
    private String vendorId;

    @Column(name = "tenant_Id", nullable = false, length = 64)
    private String tenantId;

    @Column(name = "code", nullable = false, length = 64)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "vendor_company", length = 255)
    private String vendorCompany;

    @Column(name = "vendor_category", length = 255)
    private String vendorCategory;

    @Column(name = "vendor_phone", length = 15)
    private String vendorPhone;

    @Column(name = "vendor_email", length = 255)
    private String vendorEmail;

    @Column(name = "contact_person", nullable = false, length = 100)
    private String contactPerson;

    @Column(name = "vendor_mobile_number", nullable = false, length = 15)
    private String vendorMobileNumber;

    @Column(name = "ifsc_code", length = 11)
    private String ifscCode;

    @Column(name = "bank", length = 100)
    private String bank;

    @Column(name = "bank_branch_name", length = 100)
    private String bankBranchName;

    @Column(name = "micr_no", length = 11)
    private String micrNo;

    @Column(name = "bank_account_number", length = 20)
    private String bankAccountNumber;

    @Column(name = "narration", length = 500)
    private String narration;

    @Column(name = "pan_no", length = 15)
    private String panNo;

    @Column(name = "gst_tin_no", length = 15)
    private String gstTinNo;

    @Column(name = "gst_registered_state", length = 100)
    private String gstRegisteredState;

    @Column(name = "vendor_group", nullable = false, length = 64)
    private String vendorGroup;

    @Column(name = "vendor_type", nullable = false, length = 64)
    private String vendorType;

    @Column(name = "service_type", nullable = false, length = 64)
    private String serviceType;

    @Column(name = "registration_no", length = 50)
    private String registrationNo;

    @Column(name = "registration_date", length = 50)
    private Long registrationDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "epf_no", length = 20)
    private String epfNo;

    @Column(name = "esi_no", length = 20)
    private String esiNo;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "vendor_additional_details_id", referencedColumnName = "vendor_additional_details_id")
    @JsonProperty("documents")
    private List<Document> documents;

    //@JsonIgnore
    //@JsonProperty("audit_details")
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "createdBy", column = @Column(name = "audit_created_by")),
            @AttributeOverride(name = "lastModifiedBy", column = @Column(name = "audit_last_modified_by")),
            @AttributeOverride(name = "createdTime", column = @Column(name = "audit_created_time")),
            @AttributeOverride(name = "lastModifiedTime", column = @Column(name = "audit_last_modified_time"))
    })
    @JsonUnwrapped
    private AuditDetails auditDetails;

}
