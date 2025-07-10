
package org.egov.finance.inbox.entity;

import java.io.Serializable;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

@MappedSuperclass
public abstract class AbstractPersistable<PK extends Serializable> implements Serializable {
	private static final long serialVersionUID = 7094572260034458544L;

@jakarta.persistence.Id
private Long id;

@Version
private Long version;

public Long getId() {
    return id;
}

protected void setId(Long id) {
    this.id = id;
}

public Long getVersion() {
    return version;
}

public boolean isNew() {
    return id == null;
}

public boolean notEquals(AbstractPersistable other) {
    return !this.equals(other);
}

@Override
public String toString() {
    return String.format("Entity of type %s with id: %s", getClass().getName(), id);
}}
