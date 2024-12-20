//package org.egov.schedulerservice.entity;
//
//import javax.persistence.Column;
//import javax.persistence.Id;
//import javax.persistence.Table;
//
//import org.egov.schedulerservice.model.AbstractAuditable;
//import org.javers.core.metamodel.annotation.Entity;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "scheduler_master_mapping")
//public class SchedulerMaster extends AbstractAuditable {
//	public static final String SEQ = "seq_scheduler_master_mapping";
//	private static final long serialVersionUID = 7977534010758407945L;
//	@Id
//	private Long id;
//	@Column(name = "businessservice")
//	private String businesService;
//	@Column(name = "startdate")
//	private Long startDate;
//	@Column(name = "enddate")
//	private Long endDate;
//	@Column(name = "slotsize")
//	private Long slotSize;
//	@Column(name = "dateformat")
//	private Long frequency;
//	private String dateFormat;
//
//}
