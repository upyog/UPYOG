package org.egov.schedulerservice.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.egov.schedulerservice.model.AbstractAuditable;
import org.javers.core.metamodel.annotation.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="business_service_type")
@SequenceGenerator(name = BusinessServiceType.SEQ, sequenceName = BusinessServiceType.SEQ, allocationSize = 1)
public class BusinessServiceType extends AbstractAuditable{
	public static final String SEQ = "seq_business_service_type";
	private static final long serialVersionUID = 7977534010758407945L;
	@Id
	@GeneratedValue(generator = BusinessServiceType.SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;
	@OneToMany
	@JoinColumn(name="businessid")
	private SchedulerMaster schedulerMasterid;
	@Column(name="businessservicename")
	private String businessServiceName;
	

}
