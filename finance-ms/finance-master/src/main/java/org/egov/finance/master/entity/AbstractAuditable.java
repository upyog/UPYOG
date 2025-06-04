
package org.egov.finance.master.entity;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
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
public abstract class AbstractAuditable extends AbstractPersistable<Long> {

	private static final long serialVersionUID = 7138056997693406739L;

	
	private Long createdBy;
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	private Date createdDate;
	private Long lastModifiedBy;
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	private Date lastModifiedDate;

	
}
