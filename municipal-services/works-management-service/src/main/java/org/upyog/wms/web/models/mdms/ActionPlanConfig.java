package org.upyog.wms.web.models.mdms;

import java.util.Set;

/**
 * MDMS WorksManagement.ActionPlanConfig (one entry per state). Every rule that can differ between states
 * is configured here instead of being hard-coded.
 *
 * @param workflowEnabled null = use the service property wms.workflow.enabled
 */
public record ActionPlanConfig(
        boolean verificationRequired,
        Set<String> seniorApprovalFundSources,
        boolean makerCheckerSeparation,
        boolean fullAllocationRequired,
        boolean enforceComponentCount,
        boolean technicalSanctionRequired,
        int maxSupportingDocuments,
        Boolean workflowEnabled) {

    /** Values used when the state has not configured ActionPlanConfig (FRS defaults). */
    public static ActionPlanConfig defaults() {
        return new ActionPlanConfig(true, Set.of("GOVT_GRANT", "PARTIAL"), true, true, true, true, 5, null);
    }

    public boolean requiresSeniorApproval(String fundSource) {
        return fundSource != null && seniorApprovalFundSources.contains(fundSource);
    }
}
