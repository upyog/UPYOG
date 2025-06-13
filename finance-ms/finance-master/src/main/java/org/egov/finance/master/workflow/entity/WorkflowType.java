package org.egov.finance.master.workflow.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.jpa.domain.AbstractAuditable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "EG_WF_TYPES")
@SequenceGenerator(name = WorkflowType.SEQ_WORKFLOWTYPES, sequenceName = WorkflowType.SEQ_WORKFLOWTYPES, allocationSize = 1)
public class WorkflowType extends AbstractAuditable {

    public static final String SEQ_WORKFLOWTYPES = "SEQ_EG_WF_TYPES"; // Moved up and made public

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = SEQ_WORKFLOWTYPES, strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "module")
    @Fetch(FetchMode.JOIN)
    private Module module;

    private String type;
    private String typeFQN;
    private String link;
    private String displayName;
    private boolean enabled;
    private boolean grouped;
}
