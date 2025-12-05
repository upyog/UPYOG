

package org.egov.finance.inbox.entity;

import static org.egov.finance.inbox.entity.HierarchyType.SEQ_HIERARCHY_TYPE;

import org.egov.finance.inbox.customannotation.SafeHtml;
import org.egov.finance.inbox.validation.Unique;
import org.hibernate.validator.constraints.Length;

import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Unique(fields = {"name", "code"}, enableDfltMsg = true)
@Table(name = "eg_hierarchy_type")
@Data
@SequenceGenerator(name = SEQ_HIERARCHY_TYPE, sequenceName = SEQ_HIERARCHY_TYPE, allocationSize = 1)
public class HierarchyType extends AuditDetailswithoutVersion {

    public static final String SEQ_HIERARCHY_TYPE = "SEQ_EG_HIERARCHY_TYPE";
    private static final long serialVersionUID = -7131667806935923935L;
    @Expose
    @Id
    @GeneratedValue(generator = SEQ_HIERARCHY_TYPE, strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Length(max = 128)
    @SafeHtml
    private String name;

    @NotNull
    @Length(max = 25)
    @SafeHtml
    private String code;

    @Length(max = 256)
    @SafeHtml
    private String localName;



    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof HierarchyType))
            return false;
        HierarchyType that = (HierarchyType) o;
        return Objects.equal(name, that.name) &&
                Objects.equal(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, code);
    }
}
