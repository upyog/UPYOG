/**
 * Created on Jun 2, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.model;

import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.master.entity.Fund;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
public class FundModel {
    private Long id;
    private String name; 
    private String code;
    private Character identifier;
    private BigDecimal llevel = BigDecimal.ONE;
    private Long parentId;
    private Boolean isnotleaf;
    private Boolean isactive;
    private Long createdby;
    private Date createdDate;
    private Long lastModifiedBy;
    private Date lastModifiedDate;

}

