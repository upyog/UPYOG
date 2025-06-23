package org.egov.finance.inbox.model;

import org.egov.finance.inbox.customannotation.SafeHtml;

import lombok.Data;

@Data
public class AccountDetailTypeModel {
	
	private Long id;

    @SafeHtml
    private String name;

    @SafeHtml
    private String description;

    @SafeHtml
    private String tableName;

    @SafeHtml
    private String fullyQualifiedName;

    private Boolean active;

    private Long createdBy;
    private java.util.Date createdDate;
    private Long lastModifiedBy;
    private java.util.Date lastModifiedDate;

}
