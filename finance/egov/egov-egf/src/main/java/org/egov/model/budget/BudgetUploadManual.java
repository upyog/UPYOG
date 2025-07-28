package org.egov.model.budget;

public class BudgetUploadManual {

    private String fundCode;
    private String departmentCode;
    private String functionCode;
    private String majorCode;
    private String minorCode;
    private String chartOfAccountCode;
    private String reAmount;
    private String beAmount;
    private String lastYearApproved;
    private String currentApproved;
    private String percentageChange;
    private String planningPercentage;

    public BudgetUploadManual() {
        // Default constructor required for Jackson
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public String getChartOfAccountCode() {
        return chartOfAccountCode;
    }

    public void setChartOfAccountCode(String chartOfAccountCode) {
        this.chartOfAccountCode = chartOfAccountCode;
    }
    
    public String getMajorCode() {
		return majorCode;
	}

	public void setMajorCode(String majorCode) {
		this.majorCode = majorCode;
	}

	public String getMinorCode() {
		return minorCode;
	}

	public void setMinorCode(String minorCode) {
		this.minorCode = minorCode;
	}

    public String getReAmount() {
        return reAmount;
    }

    public void setReAmount(String reAmount) {
        this.reAmount = reAmount;
    }

    public String getBeAmount() {
        return beAmount;
    }

    public void setBeAmount(String beAmount) {
        this.beAmount = beAmount;
    }

    public String getPlanningPercentage() {
        return planningPercentage;
    }

    public void setPlanningPercentage(String planningPercentage) {
        this.planningPercentage = planningPercentage;
    }
    
	public String getLastYearApproved() {
		return lastYearApproved;
	}

	public void setlastYearApproved(String lastYearApproved) {
		this.lastYearApproved = lastYearApproved;
	}

	public String getCurrentApproved() {
		return currentApproved;
	}

	public void setCurrentApproved(String currentApproved) {
		this.currentApproved = currentApproved;
	}

	public String getPercentageChange() {
		return percentageChange;
	}

	public void setPercentageChange(String percentageChange) {
		this.percentageChange = percentageChange;
	}

    @Override
    public String toString() {
        return "BudgetUploadManual{" +
                "fundCode='" + fundCode + '\'' +
                ", departmentCode='" + departmentCode + '\'' +
                ", functionCode='" + functionCode + '\'' +
                ", chartOfAccountCode='" + chartOfAccountCode + '\'' +
                ", majorCode='" + majorCode + '\'' +
                ", minorCode='" + minorCode + '\'' +
                ", reAmount='" + reAmount + '\'' +
                ", beAmount='" + beAmount + '\'' +
                ", lastYearApproved='"+lastYearApproved+'\''+
                ", currentApproved='"+currentApproved+'\''+
                ", percentageChange='"+percentageChange+'\''+
                ", planningPercentage='" + planningPercentage + '\'' +
                '}';
    }


}
