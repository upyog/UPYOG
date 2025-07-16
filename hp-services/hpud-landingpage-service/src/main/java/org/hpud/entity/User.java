package org.hpud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hpud.model.AbstractAuditable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "eg_user")
//@SequenceGenerator(name = LandingPage.SEQ, sequenceName = LandingPage.SEQ, allocationSize = 1)
public class User {
//	public static final String SEQ = "seq_landing_page";
//	private static final long serialVersionUID = 7977534010758407945L;
	@Id
//	@GeneratedValue(generator = LandingPage.SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column
	private String uuid;

	@Column
	private String mobileNumber;

	@Column(name="active")
	private Boolean active;

	@Column(name="type")
	private String type;

}
