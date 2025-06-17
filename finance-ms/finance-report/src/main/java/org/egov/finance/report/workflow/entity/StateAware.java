package org.egov.finance.report.workflow.entity;

import java.io.Serializable;

import org.egov.finance.report.entity.AuditDetailswithVersion;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class StateAware extends AuditDetailswithVersion {

}
