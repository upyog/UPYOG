package org.egov.finance.voucher.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "eg_partytype")
public class EgPartytype implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // or use appropriate strategy
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "parentid") // Adjust the column name as per your DB schema
    private EgPartytype egPartytype;

    private String code;

    private String description;

    private BigDecimal createdby;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createddate;

    private BigDecimal lastmodifiedby;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastmodifieddate;

    @OneToMany(mappedBy = "egPartytype")
    private Set<EgPartytype> egPartytypes = new HashSet<>();

    @OneToMany(mappedBy = "egPartytype") // Assuming EgwTypeOfWork has a field `egPartytype`
    private Set<EgwTypeOfWork> egwTypeofworks = new HashSet<>();

    public EgPartytype() {
    }

    public EgPartytype(String code, String description, BigDecimal createdby, Date createddate) {
        this.code = code;
        this.description = description;
        this.createdby = createdby;
        this.createddate = createddate;
    }

    public EgPartytype(EgPartytype egPartytype, String code, String description, BigDecimal createdby, Date createddate,
                       BigDecimal lastmodifiedby, Date lastmodifieddate, Set<EgwTypeOfWork> egwTypeofworks,
                       Set<EgPartytype> egPartytypes) {
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
}