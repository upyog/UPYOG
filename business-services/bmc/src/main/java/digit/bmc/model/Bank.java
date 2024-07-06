package digit.bmc.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
@Entity
@Data
@Table(name="bank")
public class Bank {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    @Column(name = "id")
	    private Long id;

	    @Column(name = "code")
	    private String code;

	    @Column(name = "name")
	    private String name;

	    @Column(name = "narration")
	    private String narration;

	    @Column(name = "isactive")
	    private Boolean isActive;

	    @Column(name = "type")
	    private String type;

	    @Column(name = "createddate")
	    
	    private Timestamp createdDate;

	    @Column(name = "lastmodifieddate")
	    private Timestamp lastModifiedDate;

	    @Column(name = "lastmodifiedby")
	    private Long lastModifiedBy;

	    @Column(name = "version")
	    private Long version;

	    @Column(name = "createdby")
	    private Long createdBy;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getNarration() {
			return narration;
		}

		public void setNarration(String narration) {
			this.narration = narration;
		}

		public Boolean getIsActive() {
			return isActive;
		}

		public void setIsActive(Boolean isActive) {
			this.isActive = isActive;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Timestamp getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(Timestamp createdDate) {
			this.createdDate = createdDate;
		}

		public Timestamp getLastModifiedDate() {
			return lastModifiedDate;
		}

		public void setLastModifiedDate(Timestamp lastModifiedDate) {
			this.lastModifiedDate = lastModifiedDate;
		}

		public Long getLastModifiedBy() {
			return lastModifiedBy;
		}

		public void setLastModifiedBy(Long lastModifiedBy) {
			this.lastModifiedBy = lastModifiedBy;
		}

		public Long getVersion() {
			return version;
		}

		public void setVersion(Long version) {
			this.version = version;
		}

		public Long getCreatedBy() {
			return createdBy;
		}

		public void setCreatedBy(Long createdBy) {
			this.createdBy = createdBy;
		}

		@Override
		public String toString() {
			return "Bank [id=" + id + ", code=" + code + ", name=" + name + ", narration=" + narration + ", isActive="
					+ isActive + ", type=" + type + ", createdDate=" + createdDate + ", lastModifiedDate="
					+ lastModifiedDate + ", lastModifiedBy=" + lastModifiedBy + ", version=" + version + ", createdBy="
					+ createdBy + "]";
		}

	   // code 
	    


}
