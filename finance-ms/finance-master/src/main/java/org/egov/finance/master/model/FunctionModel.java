/**
 * Created on Jun 9, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.model;

import java.util.Date;

import org.egov.finance.master.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class FunctionModel {
	
    private Long id;
    @Length(max = 100, min = 2)
    @SafeHtml
    private String name;
    @Length(max = 50, min = 2)
    @SafeHtml
    private String code;
    @Length(max = 50)
    @SafeHtml
    private String type;
    private int llevel;
    private Boolean isActive;
    private Boolean isNotLeaf;
    private Long parentId;
    
    ////////////////AUDIT/////////////
    private Long createdBy;
	private Date createdDate;
	private Long lastModifiedBy;
	private Date lastModifiedDate;



}

