package digit.web.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "eg_bmc_userqualification")
public class EgBmcUserQualification {

    @Id
    private Long id;

    
    @JoinColumn(name = "userID")
    private Long userID;
    @JoinColumn(name = "qualificatioID")
    private Long qualificatioID;

    @Column(name = "createdOn")
    private Long createdOn;

    @Column(name = "modifiedOn")
    private Long modifiedOn;

    @Column(name = "createdBy")
    private String createdBy;

    @Column(name ="modifiedBy")
    private String modifiedBy;

}
