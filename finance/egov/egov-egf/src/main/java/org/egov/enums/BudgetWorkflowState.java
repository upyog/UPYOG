package org.egov.enums;

/**
 * Enumeration representing budget workflow states and their corresponding
 * display representations for active desks and historical audit logs.
 */
public enum BudgetWorkflowState {

    NEW("Created", "Created"),
    CREATED("Created", "Created"),
    PENDING_EO_APPROVAL("Pending EO Approval", "Approved"),
    FORWARDED_TO_DMA("Pending DMA Approval", "Approved"),
    DMA_APPROVED("Approved", "Approved"),
    APPROVED("Approved", "Approved"),
    REVERTED("Reverted", "Reverted"),
    REJECTED("Rejected", "Rejected");

    private final String currentDisplayStatus;
    private final String historyDisplayStatus;

    BudgetWorkflowState(final String currentDisplayStatus, final String historyDisplayStatus) {
        this.currentDisplayStatus = currentDisplayStatus;
        this.historyDisplayStatus = historyDisplayStatus;
    }

    /**
     * Resolves a raw string status to a {@link BudgetWorkflowState} constant (case-insensitive).
     *
     * @param status the raw string status from database or request
     * @return matching BudgetWorkflowState, or null if unrecognized
     */
    public static BudgetWorkflowState from(final String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }
        for (final BudgetWorkflowState s : values()) {
            if (s.name().equalsIgnoreCase(status.trim())) {
                return s;
            }
        }
        return null;
    }

    public String getCurrentDisplayStatus() {
        return currentDisplayStatus;
    }

    public String getHistoryDisplayStatus() {
        return historyDisplayStatus;
    }
}
