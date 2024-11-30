package org.egov.schedulerservice.model;

import java.util.Date;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AbstractAuditable extends AbstractPersistable<Long> {

	private static final long serialVersionUID = 7138056997693406739L;

	private Long createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	private Date createdDate;

	private Long lastModifiedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	private Date lastModifiedDate;

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(final Long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	@Audited
	public Long getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(final Long lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	@Audited
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
