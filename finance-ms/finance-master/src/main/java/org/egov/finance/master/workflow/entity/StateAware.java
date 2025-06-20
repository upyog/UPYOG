package org.egov.finance.master.workflow.entity;

import java.io.Serializable;

import org.egov.finance.master.entity.AuditDetailswithVersion;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class StateAware extends AuditDetailswithVersion {

}
