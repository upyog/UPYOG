
package org.egov.finance.master.entity;

import java.io.Serializable;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

@MappedSuperclass
public abstract class AbstractPersistable<PK extends Serializable> implements Serializable {

    private static final long serialVersionUID = 7094572260034458544L;

    @Version
    private Long version;

    public abstract PK getId();

    protected abstract void setId(PK id);

    public Long getVersion() {
        return version;
    }

    public boolean isNew() {
        return null == getId();
    }

    public boolean notEquals(AbstractPersistable persistable) {
        return !this.equals(persistable);
    }

    @Override
    public String toString() {
        return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
    }
}
