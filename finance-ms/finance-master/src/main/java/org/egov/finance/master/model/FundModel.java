/**
 * Created on Jun 2, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.model;

import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.master.customannotation.SafeHtml;
import org.egov.finance.master.entity.Fund;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Data
public class FundModel {
    private Long id;
    
    @Length(max = 50, min = 2)
	@SafeHtml
	@NotNull
	@JsonProperty("name")
    private String name; 
    
    @Length(max = 50, min = 2)
	@SafeHtml
	@NotNull
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

