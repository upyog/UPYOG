
package org.egov.finance.report.entity;

import static org.egov.finance.report.entity.Role.SEQ_ROLE;

import java.util.Objects;

import org.egov.finance.report.customannotation.SafeHtml;
import org.egov.finance.report.validation.Unique;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.jpa.domain.AbstractAuditable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Unique(fields = "name", enableDfltMsg = true)
@Table(name = "eg_role")
@SequenceGenerator(name = SEQ_ROLE, sequenceName = SEQ_ROLE, allocationSize = 1)
public class Role extends AuditDetailswithVersion {

    public static final String SEQ_ROLE = "SEQ_EG_ROLE";
    private static final long serialVersionUID = 7034114743461088547L;
    @Id
    @GeneratedValue(generator = SEQ_ROLE, strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank
    @SafeHtml
    @Length(max = 32)
    private String name;

    @SafeHtml
    @Length(max = 150)
    private String description;

    private boolean internal;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(final boolean internal) {
        this.internal = internal;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final Role role = (Role) o;
        return Objects.equals(getName(), role.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
