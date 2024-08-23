package digit.bmc.model;

import java.sql.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "eg_bmc_aadharuser")
public class AadharUser {

    @Id
    private Long id;
    private String aadharRef;
    private Long userId;
    private String tenantId;
    @JsonProperty("father")
    private String aadharFatherName;
    @JsonProperty("aadharName")
    private String aadharName;
    @JsonProperty("dob")
    private Date aadharDob;
    private String aadharMobile;
    @JsonProperty("gender")
    private String gender;
    private Long createdOn;
    private Long modifiedOn;
    private String createdBy;
    private String modifiedBy;

}
