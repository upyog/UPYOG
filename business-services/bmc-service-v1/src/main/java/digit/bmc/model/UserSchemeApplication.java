package digit.bmc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "eg_bmc_userschemeapplication")
public class UserSchemeApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "applicationnumber")
    private String applicationNumber;

    @Column(name = "userid")
    private Long userId;

    @Column(name = "tenantid")
    private String tenantId;

    @Column(name = "optedid")
    private Long optedId;

    @Column(name = "applicationstatus")
    private Boolean applicationStatus;

    @Column(name = "verificationstatus")
    private Boolean verificationStatus;

    @Column(name = "firstapprovalstatus")
    private Boolean firstApprovalStatus;

    @Column(name = "randomselection")
    private Boolean randomSelection;

    @Column(name = "finalapproval")
    private Boolean finalApproval;

    @Column(name = "submitted")
    private Boolean submitted;

    @Column(name = "modifiedon")
    private Long modifiedOn;

    @Column(name = "createdby")
    private String createdBy;

    @Column(name = "modifiedby")
    private String modifiedBy;

}
