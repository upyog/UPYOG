package digit.common;

public enum CriteriaType {

    GENDER("Gender"),
    DISABILITY("Disability"),
    AGE("Age"),
    INCOME("Income"),
    EDUCATION("Education"),
    DOCUMENT("Document"),
    BENIFITTED("Benifitted");

    private final String value;

    CriteriaType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public static CriteriaType fromDisplayName(String value) {
        for (CriteriaType criteriaType : CriteriaType.values()) {
            if (criteriaType.value.equals(value)) {
                return criteriaType;
            }
        }
        throw new IllegalArgumentException("Unknown display name: " + value);
    }

}
