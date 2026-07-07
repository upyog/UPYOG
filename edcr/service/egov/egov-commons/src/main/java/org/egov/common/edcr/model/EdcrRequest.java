package org.egov.common.edcr.model;

import java.io.File;
import java.util.Date;

import org.egov.infra.microservice.models.RequestInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EdcrRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    private String applicantName;

    private String transactionNumber;

    private String edcrNumber;

    private String permitNumber;

    private Date permitDate;

    private File planFile;

    private String tenantId;

    private String appliactionType;
    
    private String coreArea;

    private String applicationSubType;

    private String comparisonEdcrNumber;

    private String status;

    private Date fromDate;

    private Date toDate;
    
    private String applicationNumber;

    private Integer offset;

    private Integer limit;
    
    private String orderBy;
    
    //New fields based on initial form
    private String ulb;
    private String areaType;
    private String schemeArea;
    private String schName;
    private Boolean siteReserved;
    private Boolean approvedCS;      
    private Boolean cluApprove;
    
    private Boolean purchasableFar;
    
    //private File layoutFile;  
    
	@JsonProperty("additionalDetails")
	private Object additionalDetails = null;

	public Object getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(Object additionalDetails) {
		this.additionalDetails = additionalDetails;
	}

    public Boolean getPurchasableFar() {
		return purchasableFar;
	}


	public void setPurchasableFar(Boolean purchasableFar) {
		this.purchasableFar = purchasableFar;
	}


	public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    
    public String getUlb() {
		return ulb;
	}

	public void setUlb(String ulb) {
		this.ulb = ulb;
	}

	public String getAreaType() {
		return areaType;
	}

	public void setAreaType(String areaType) {
		this.areaType = areaType;
	}

	public String getSchemeArea() {
		return schemeArea;
	}

	public void setSchemeArea(String schemeArea) {
		this.schemeArea = schemeArea;
	}

	public String getSchName() {
		return schName;
	}

	public void setSchName(String schName) {
		this.schName = schName;
	}

	public Boolean getSiteReserved() {
		return siteReserved;
	}

	public void setSiteReserved(Boolean siteReserved) {
		this.siteReserved = siteReserved;
	}

	public Boolean getApprovedCS() {
		return approvedCS;
	}

	public void setApprovedCS(Boolean approvedCS) {
		this.approvedCS = approvedCS;
	}

	public Boolean getCluApprove() {
		return cluApprove;
	}

	public void setCluApprove(Boolean cluApprove) {
		this.cluApprove = cluApprove;
	}

	public void setRequestInfo(RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }
    
    public String getCoreArea() {
        return coreArea;
    }

    public void setCoreArea(String coreArea) {
        this.coreArea = coreArea;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public File getPlanFile() {
        return planFile;
    }

    public void setPlanFile(File planFile) {
        this.planFile = planFile;
    }

    public String getEdcrNumber() {
        return edcrNumber;
    }

    public void setEdcrNumber(String edcrNumber) {
        this.edcrNumber = edcrNumber;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getPermitNumber() {
        return permitNumber;
    }

    public void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }

    public Date getPermitDate() {
        return permitDate;
    }

    public void setPermitDate(Date permitDate) {
        this.permitDate = permitDate;
    }

    public String getAppliactionType() {
        return appliactionType;
    }

    public void setAppliactionType(String appliactionType) {
        this.appliactionType = appliactionType;
    }

    public String getApplicationSubType() {
        return applicationSubType;
    }

    public void setApplicationSubType(String applicationSubType) {
        this.applicationSubType = applicationSubType;
    }

    public String getComparisonEdcrNumber() {
        return comparisonEdcrNumber;
    }

    public void setComparisonEdcrNumber(String comparisonEdcrNumber) {
        this.comparisonEdcrNumber = comparisonEdcrNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

}
