package org.egov.finance.master.entity;

import org.springframework.data.jpa.domain.AbstractAuditable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "chequeformat")
@SequenceGenerator(name = ChequeFormat.SEQ, sequenceName = ChequeFormat.SEQ, allocationSize = 1)
public class ChequeFormat extends AuditDetailswithVersion {
    
    private static final long serialVersionUID = 1L;
    public static final String SEQ = "seq_chequeformat";

    @Id
    @GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
    private Long id;

    private String chequeName;
    private String chequeType;
    private Double chequeLength;
    private Double chequeWidth;
    private String accountPayeeCoordinate;
    private String dateFormat;
    private String dateCoordinate;
    private Double payeeNameLength;
    private String payeeNameCoordinate;
    private String amountNumberingFormat;
    private String amountInWordsFirstLineCoordinate;
    private Double amountInWordsFirstLineLength;
    private Double amountInWordsSecondLineLength;
    private String amountInWordsSecondLineCoordinate;
    private Double amountLength;
    private String amountCoordinate;
    private boolean formatStatus;
}
