package org.egov.finance.voucher.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "egw_typeofwork") 
public class EgwTypeOfWork implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // or use appropriate strategy
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

    @Temporal(TemporalType.TIMESTAMP)
    private Date createddate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastmodifieddate;
}
