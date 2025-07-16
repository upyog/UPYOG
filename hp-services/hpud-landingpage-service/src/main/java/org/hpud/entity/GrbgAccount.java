package org.hpud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

//import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter

@Table(name = "eg_grbg_account")
public class GrbgAccount {
	@Id
//	@GeneratedValue(generator = LandingPage.SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column
	private String user_uuid;

	@Column
	private String mobile_number;
}
