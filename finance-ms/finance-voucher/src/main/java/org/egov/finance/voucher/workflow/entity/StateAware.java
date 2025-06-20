package org.egov.finance.voucher.workflow.entity;

import java.io.Serializable;

import org.egov.finance.voucher.entity.AuditDetailswithVersion;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class StateAware extends AuditDetailswithVersion {

}
