
package org.egov.finance.report.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
@SequenceGenerator(name = EgPartytype.SEQ_PARTYTYPE, sequenceName = EgPartytype.SEQ_PARTYTYPE, allocationSize = 1)
public class EgPartytype implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public static final String SEQ_PARTYTYPE = "SEQ_PARTYTYPE";

	@Id
	@GeneratedValue(generator = EgPartytype.SEQ_PARTYTYPE, strategy = GenerationType.SEQUENCE)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentid")
	private EgPartytype egPartytype;

	private String code;

	private String description;

	private BigDecimal createdby;

	private Date createddate;

	private BigDecimal lastmodifiedby;

	private Date lastmodifieddate;
	
	@OneToMany(mappedBy = "egPartytype", fetch = FetchType.LAZY)
	private Set<EgwTypeOfWork> egwTypeofworks = new HashSet<EgwTypeOfWork>(0);
	

	@OneToMany(mappedBy = "egPartytype", fetch = FetchType.LAZY)
	private Set<EgPartytype> egPartytypes = new HashSet<EgPartytype>(0);

	public EgPartytype() {
	}

	public EgPartytype(String code, String description, BigDecimal createdby, Date createddate) {
		this.code = code;
		this.description = description;
		this.createdby = createdby;
		this.createddate = createddate;
	}

	public EgPartytype(EgPartytype egPartytype, String code, String description, BigDecimal createdby, Date createddate, BigDecimal lastmodifiedby, Date lastmodifieddate, Set<EgwTypeOfWork> egwTypeofworks, Set<EgPartytype> egPartytypes) {
		this.egPartytype = egPartytype;
		this.code = code;
		this.description = description;
		this.createdby = createdby;
		this.createddate = createddate;
		this.lastmodifiedby = lastmodifiedby;
		this.lastmodifieddate = lastmodifieddate;
		this.egwTypeofworks = egwTypeofworks;
		this.egPartytypes = egPartytypes;
	}

	public EgPartytype getEgPartytype() {
		return this.egPartytype;
	}

	public void setEgPartytype(EgPartytype egPartytype) {
		this.egPartytype = egPartytype;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getCreatedby() {
		return this.createdby;
	}

	public void setCreatedby(BigDecimal createdby) {
		this.createdby = createdby;
	}

	public Date getCreateddate() {
		return this.createddate;
	}

	public void setCreateddate(Date createddate) {
		this.createddate = createddate;
	}

	public BigDecimal getLastmodifiedby() {
		return this.lastmodifiedby;
	}

	public void setLastmodifiedby(BigDecimal lastmodifiedby) {
		this.lastmodifiedby = lastmodifiedby;
	}

	public Date getLastmodifieddate() {
		return this.lastmodifieddate;
	}

	public void setLastmodifieddate(Date lastmodifieddate) {
		this.lastmodifieddate = lastmodifieddate;
	}

	public Set<EgwTypeOfWork> getEgwTypeofworks() {
		return this.egwTypeofworks;
	}

	public void setEgwTypeofworks(Set<EgwTypeOfWork> egwTypeofworks) {
		this.egwTypeofworks = egwTypeofworks;
	}

	public Set<EgPartytype> getEgPartytypes() {
		return this.egPartytypes;
	}

	public void setEgPartytypes(Set<EgPartytype> egPartytypes) {
		this.egPartytypes = egPartytypes;
	}

}
