package digit.web.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "eg_bmc_qualification_details")

public class Qualification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "passingYear")
    private Long passingYear;

    @Column(name = "percentage")
    private BigDecimal percentage;

    @Column(name = "bord")
    private String bord;

    @Column(name = "createdOn")
    private Long createdOn;

    @Column(name = "modifiedOn")
    private Long modifiedOn;

    @Column(name = "createdBy")
    private String createdBy;

    @Column(name ="modifiedBy")
    private String modifiedBy;

}
