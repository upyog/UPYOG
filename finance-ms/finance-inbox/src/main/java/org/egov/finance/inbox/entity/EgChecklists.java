
package org.egov.finance.inbox.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "EG_CHECKLISTS")
/*
 * @NamedQueries({
 * 
 * @NamedQuery(name = "checklist.by.appconfigid.and.objectid", query =
 * "from EgChecklists as checkList where checkList.objectid =? and checkList.appconfigvalue.config.id in(?)"
 * ),
 * 
 * @NamedQuery(name = "checklist.by.objectid", query =
 * "from EgChecklists as checkList where checkList.objectid =?") })
 */
@SequenceGenerator(name = EgChecklists.SEQ_EG_CHECKLISTS, sequenceName = EgChecklists.SEQ_EG_CHECKLISTS, allocationSize = 1)
public class EgChecklists extends AuditDetailswithoutVersion  {

    private static final long serialVersionUID = -3245474955686333063L;

    public static final String SEQ_EG_CHECKLISTS = "SEQ_EG_CHECKLISTS";

    @Id
    @GeneratedValue(generator = SEQ_EG_CHECKLISTS, strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "OBJECT_ID")
    private Long objectid;

    private String checklistvalue;

    @ManyToOne
    @JoinColumn(name = "APPCONFIG_VALUES_ID", nullable = false)
    private AppConfigValues appconfigvalue;

    @Transient
    private String remarks;

}
