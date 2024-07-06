package digit.bmc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "eg_bmc_schemecourse")
public class SchemeCourse {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "schemeid")
    private Integer schemeId;

    @Column(name = "courseid")
    private Integer courseId;

    @Column(name = "grantamount")
    private Double grantAmount;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSchemeId() {
		return schemeId;
	}

	public void setSchemeId(Integer schemeId) {
		this.schemeId = schemeId;
	}

	public Integer getCourseId() {
		return courseId;
	}

	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}

	public Double getGrantAmount() {
		return grantAmount;
	}

	public void setGrantAmount(Double grantAmount) {
		this.grantAmount = grantAmount;
	}

    // Getters and Setters
    
    

}
