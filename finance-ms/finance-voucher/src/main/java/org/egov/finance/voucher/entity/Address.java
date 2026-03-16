package org.egov.finance.voucher.entity;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.egov.finance.voucher.enumeration.AddressType;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "eg_address")
@Inheritance(strategy = InheritanceType.JOINED)

@Cacheable
@Data
@ToString
public abstract class Address extends AbstractPersistable<Long> {

	public static final String SEQ_ADDRESS = "seq_eg_address";
	private static final long serialVersionUID = 4842889134725565148L;
	@SequenceGenerator(name = SEQ_ADDRESS, sequenceName = SEQ_ADDRESS, allocationSize = 1)
	@Expose
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_ADDRESS)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userid")
	private User user;

	@SafeHtml
	private String houseNoBldgApt;

	@SafeHtml
	private String streetRoadLine;

	@SafeHtml
	private String landmark;

	@SafeHtml
	private String areaLocalitySector;

	@SafeHtml
	private String cityTownVillage;

	@SafeHtml
	private String district;

	@SafeHtml
	private String subdistrict;

	@SafeHtml
	private String postOffice;

	@SafeHtml
	private String state;

	@SafeHtml
	private String country;

	@SafeHtml
	private String pinCode;

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private AddressType type;
}
