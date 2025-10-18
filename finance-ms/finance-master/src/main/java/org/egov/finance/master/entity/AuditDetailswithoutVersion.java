
package org.egov.finance.master.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class AuditDetailswithoutVersion implements Serializable {

	private static final long serialVersionUID = -1904617415925139035L;
	
	@CreatedBy
	private Long createdBy;
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	private Date createdDate;
	@LastModifiedBy
	private Long lastModifiedBy;
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	private Date lastModifiedDate;

	
}
