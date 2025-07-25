
package org.egov.finance.inbox.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
@SequenceGenerator(name = EgwTypeOfWork.SEQ_EGWTYPEOFWORK, sequenceName = EgwTypeOfWork.SEQ_EGWTYPEOFWORK, allocationSize = 1)
public class EgwTypeOfWork implements java.io.Serializable{
	
    private static final long serialVersionUID = 1L;
    public static final String SEQ_EGWTYPEOFWORK = "SEQ_EGWTYPEOFWORK";

    @Id
    @GeneratedValue(generator = SEQ_EGWTYPEOFWORK, strategy = GenerationType.SEQUENCE)
    private Long id;
	private String code;
	@ManyToOne
    @JoinColumn(name = "parentid")
    private EgwTypeOfWork parentid;

    @ManyToOne
    @JoinColumn(name = "partytypeid")
    private EgPartytype egPartytype;
	private String description;
	private Integer createdby;
	private Integer lastmodifiedby;
	private Date createddate;
	private Date lastmodifieddate;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the createdby
	 */
	public Integer getCreatedby() {
		return createdby;
	}

	/**
	 * @param createdby the createdby to set
	 */
	public void setCreatedby(Integer createdby) {
		this.createdby = createdby;
	}

	/**
	 * @return the createddate
	 */
	public Date getCreateddate() {
		return createddate;
	}

	/**
	 * @param createddate the createddate to set
	 */
	public void setCreateddate(Date createddate) {
		this.createddate = createddate;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	private void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the lastmodifiedby
	 */
	public Integer getLastmodifiedby() {
		return lastmodifiedby;
	}

	/**
	 * @param lastmodifiedby the lastmodifiedby to set
	 */
	public void setLastmodifiedby(Integer lastmodifiedby) {
		this.lastmodifiedby = lastmodifiedby;
	}

	/**
	 * @return the lastmodifieddate
	 */
	public Date getLastmodifieddate() {
		return lastmodifieddate;
	}

	/**
	 * @param lastmodifieddate the lastmodifieddate to set
	 */
	public void setLastmodifieddate(Date lastmodifieddate) {
		this.lastmodifieddate = lastmodifieddate;
	}

	/**
	 * @return the parentid
	 */
	public EgwTypeOfWork getParentid() {
		return parentid;
	}

	/**
	 * @param parentid the parentid to set
	 */
	public void setParentid(EgwTypeOfWork parentid) {
		this.parentid = parentid;
	}

	/**
	 * @return the egPartytype
	 */
	public EgPartytype getEgPartytype() {
		return egPartytype;
	}

	/**
	 * @param egPartytype the egPartytype to set
	 */
	public void setEgPartytype(EgPartytype egPartytype) {
		this.egPartytype = egPartytype;
	}

}
