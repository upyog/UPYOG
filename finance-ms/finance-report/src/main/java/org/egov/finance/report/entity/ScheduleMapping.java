/**
 * @author bpattanayak
 * @date 1 Jul 2025
 */

package org.egov.finance.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "schedulemapping")
@SequenceGenerator(name = ScheduleMapping.SEQ, sequenceName = ScheduleMapping.SEQ, allocationSize = 1)
@Data
public class ScheduleMapping extends AuditDetailswithoutVersion{
	
	private static final long serialVersionUID = -7000350285854582891L;

	public static final String SEQ = "seq_schedulemapping";

    @Id
    @GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "schedule")
    private String schedule;

    @Column(name = "schedulename")
    private String scheduleName;

    @Column(name = "reporttype")
    private String reportType;

    @Column(name="repsubtype")
    private String repsubtype;
    
    @Column(name="isremission")
    private Integer isremission;
}

