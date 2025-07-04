package org.egov.finance.voucher.entity;

import java.math.BigDecimal;
import java.util.Date;



import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity(name="eg_remittance_gl")
@SequenceGenerator(name = EgRemittanceGl.SEQ_EGREMITTANCE_GL, sequenceName = EgRemittanceGl.SEQ_EGREMITTANCE_GL, allocationSize = 1)
@Data
@ToString(exclude = {"glid", "recovery"})
@EqualsAndHashCode(exclude = {"glid", "recovery"})
public class EgRemittanceGl implements java.io.Serializable {
    private static final long serialVersionUID = -226329871221883883L;
    public static final String SEQ_EGREMITTANCE_GL = "seq_eg_remittance_gl";

    @Id
    @GeneratedValue(generator = SEQ_EGREMITTANCE_GL, strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "glid")
    private CGeneralLedger glid;

    private BigDecimal glamt;

    private Date lastmodifieddate;

    private BigDecimal remittedamt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tdsid")
    private Recovery recovery;
}
