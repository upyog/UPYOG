package org.egov.model.budget;

import java.util.List;

public class ManualWrapper {

    private String municipalityName;
    private String reYear;
    private String beYear;
    private List<BudgetUploadManual> budgetRows;

    public ManualWrapper() {
        // No-arg constructor for Jackson
    }

    public String getMunicipalityName() {
        return municipalityName;
    }

    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
    }

    public String getReYear() {
        return reYear;
    }

    public void setReYear(String reYear) {
        this.reYear = reYear;
    }

    public String getBeYear() {
        return beYear;
    }

    public void setBeYear(String beYear) {
        this.beYear = beYear;
    }

    public List<BudgetUploadManual> getBudgetRows() {
        return budgetRows;
    }

    public void setBudgetRows(List<BudgetUploadManual> budgetRows) {
        this.budgetRows = budgetRows;
    }

    @Override
    public String toString() {
        return "ManualWrapper{" +
                "municipalityName='" + municipalityName + '\'' +
                ", reYear='" + reYear + '\'' +
                ", beYear='" + beYear + '\'' +
                ", budgetRows=" + budgetRows +
                '}';
    }
}
