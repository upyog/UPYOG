
package org.egov.finance.report.entity;

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

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class AuditDetailswithVersion implements Serializable {

	private static final long serialVersionUID = 7138056997693406739L;

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
