package digit.bmc.model;

import digit.web.models.Religion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Data
@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "eg_bmc_userotherdetails")
public class UserOtherDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "userid")
    private Long userId;

    @Column(name = "tenantid")
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casteid", referencedColumnName = "id")
    private Caste caste;

    // @Column(name = "religionid")
    // private Integer religionId;
   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "religionid", referencedColumnName = "id")
    private Religion religion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "divyangid", referencedColumnName = "id")
    private Divyang divyang;

    @Column(name = "divyangcardid")
    private String divyangCardId;

    @Column(name = "divyangpercent")
    private Double divyangPercent;

    @Column(name = "transgenderid")
    private String transgenderId;

    @Column(name = "createdon")
    private Long createdOn;

    @Column(name = "modifiedon")
    private Long modifiedOn;

    @Column(name = "createdby")
    private String createdBy;

    @Column(name = "modifiedby")
    private String modifiedBy;

    @Column(name = "rationcardcategory")
    private String rationCardCategory;

    @Column(name = "educationlevel")
    private String educationLevel;

    @Column(name = "udid")
    private String udid;


    private String zone;

    private String ward;

    private String block;

}
