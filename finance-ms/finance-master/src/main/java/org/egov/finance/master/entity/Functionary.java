package org.egov.finance.master.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "functionary")
@SequenceGenerator(name = "seq_functionary", sequenceName = "seq_functionary", allocationSize = 1)
public class Functionary implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "seq_functionary", strategy = GenerationType.SEQUENCE)
    private Integer id;

    private BigDecimal code;

    private String name;

    private Date createtimestamp;

    private Date updatetimestamp;

    private Boolean isactive;
}
