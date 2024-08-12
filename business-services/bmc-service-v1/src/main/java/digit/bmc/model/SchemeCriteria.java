package digit.bmc.model;

public class SchemeCriteria {
    
    private String criteriaType;
    private String criteriaValue;
    private String criteriaCondition;
    
    public String getCriteriaType() {
        return criteriaType;
    }
    public void setCriteriaType(String criteriaType) {
        this.criteriaType = criteriaType;
    }
    public String getCriteriaValue() {
        return criteriaValue;
    }

    public void setCriteriaValue(String criteriaValue) {
        this.criteriaValue = criteriaValue;
    }

    public String getCriteriaCondition() {
        return criteriaCondition;
    }

    public void setCriteriaCondition(String criteriaCondition) {
        this.criteriaCondition = criteriaCondition;
    }

}
