package org.egov.finance.report.workflow.entity;

import org.egov.finance.report.entity.AuditDetailswithVersion;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "EG_WF_TYPES")
@SequenceGenerator(name = WorkflowType.SEQ_WORKFLOWTYPES, sequenceName = WorkflowType.SEQ_WORKFLOWTYPES, allocationSize = 1)
@Data
public class WorkflowType extends  AuditDetailswithVersion {

    public static final String SEQ_WORKFLOWTYPES = "SEQ_EG_WF_TYPES"; // Moved up and made public

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = SEQ_WORKFLOWTYPES, strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "module")
    @Fetch(FetchMode.JOIN)
    private org.egov.finance.report.entity.Module module;

    private String type;
    private String typeFQN;
    private String link;
    private String displayName;
    private boolean enabled;
    private boolean grouped;
}
