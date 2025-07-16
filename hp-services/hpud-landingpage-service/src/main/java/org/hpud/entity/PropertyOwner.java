package org.hpud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

@Table(name = "eg_pt_owner")
public class PropertyOwner {

	@Id
	private String ownerinfouuid;
	
	@Column
	private String userid;

	@Column
	private String mobile_number;
	
}
