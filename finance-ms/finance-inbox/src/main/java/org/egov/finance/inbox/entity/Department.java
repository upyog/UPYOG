

package org.egov.finance.inbox.entity;

import static org.egov.finance.inbox.entity.Department.SEQ_DEPARTMENT;

import org.egov.finance.inbox.customannotation.SafeHtml;
import org.egov.finance.inbox.validation.Unique;
import org.hibernate.validator.constraints.Length;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Unique(fields = {"name", "code"}, enableDfltMsg = true)
@Table(name = "eg_department")
@SequenceGenerator(name = SEQ_DEPARTMENT, sequenceName = SEQ_DEPARTMENT, allocationSize = 1)
@Builder

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter


public class Department extends AuditDetailswithVersion {

    public static final String SEQ_DEPARTMENT = "SEQ_EG_DEPARTMENT";
    private static final long serialVersionUID = 7630238192598939863L;
    @Expose
    @Id
    @GeneratedValue(generator = SEQ_DEPARTMENT, strategy = GenerationType.SEQUENCE)
    private Long id;

    @Length(min = 1, max = 128)
    @SafeHtml
    private String name;

    @NotNull
    @Length(min = 1, max = 128)
    @SafeHtml
    private String code;

   

  

}
