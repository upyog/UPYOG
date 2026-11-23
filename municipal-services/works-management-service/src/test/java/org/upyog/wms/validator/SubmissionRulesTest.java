package org.upyog.wms.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.upyog.wms.validator.TestData.TODAY;
import static org.upyog.wms.validator.TestData.ZONE;
import static org.upyog.wms.util.WmsUtil.projectComponents;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.upyog.wms.constants.ErrorCodes;
import org.upyog.wms.web.models.ActionPlan;
import org.upyog.wms.web.models.ValidationWarning;
import org.upyog.wms.web.models.mdms.ActionPlanConfig;
import org.upyog.wms.web.models.mdms.MdmsData;

class SubmissionRulesTest {

    private ValidationErrors run(ActionPlan plan, MdmsData mdms) {
        ValidationErrors errors = new ValidationErrors();
        new SubmissionRules(mdms, ZONE, TODAY).validate(plan, errors);
        return errors;
    }

    private ValidationErrors run(ActionPlan plan) {
        return run(plan, TestData.mdms());
    }

    @Test
    void validPlanPasses() {
        ValidationErrors errors = run(TestData.validPlan());
        assertTrue(errors.isEmpty(), () -> "Unexpected errors: " + errors.asMap());
    }

    @Test
    void componentTotalMustMatchApprovedAmount() {
        ActionPlan plan = TestData.validPlan();
        projectComponents(plan).get(2).setApprovedAmount(new BigDecimal("30"));
        projectComponents(plan).get(2).getFundDetails().get(0).setAmount(new BigDecimal("30"));
        ValidationErrors errors = run(plan);
        assertTrue(errors.containsRule(ErrorCodes.COMPONENT_TOTAL_MISMATCH), errors.asMap()::toString);
    }

    @Test
    void approvedAmountMustComeFromActionPlan() {
        ActionPlan plan = TestData.validPlan();
        plan.getProjectDetails().get(0).setApprovedAmount(new BigDecimal("999"));

        ValidationErrors errors = run(plan);

        assertTrue(errors.isEmpty(), errors.asMap()::toString);
    }

    @Test
    void actionPlanApprovedAmountIsMandatory() {
        ActionPlan plan = TestData.validPlan();
        plan.setApprovedAmount(null);

        ValidationErrors errors = run(plan);

        assertTrue(errors.asMap().containsKey(ErrorCodes.REQUIRED + ":approvedAmount"),
                errors.asMap()::toString);
    }

    @Test
    void underAllocationAllowedWhenFullAllocationIsOff() {
        ActionPlanConfig cfg = new ActionPlanConfig(true, Set.of("GOVT_GRANT", "PARTIAL"), true, false, true, true, 5, null);
        ActionPlan plan = TestData.validPlan();
        projectComponents(plan).get(2).setApprovedAmount(new BigDecimal("30"));
        projectComponents(plan).get(2).getFundDetails().get(0).setAmount(new BigDecimal("30"));
        assertTrue(run(plan, TestData.mdms(cfg)).isEmpty());

        projectComponents(plan).get(2).setApprovedAmount(new BigDecimal("50"));
        projectComponents(plan).get(2).getFundDetails().get(0).setAmount(new BigDecimal("50"));
        assertTrue(run(plan, TestData.mdms(cfg)).containsRule(ErrorCodes.COMPONENT_OVER_ALLOCATED));
    }

    @Test
    void planLevelFundDetailsAreValidatedAgainstActionPlanAmount() {
        ActionPlan plan = TestData.validPlan();
        plan.setFundDetails(List.of());
        ValidationErrors errors = run(plan);
        assertTrue(errors.asMap().containsKey(ErrorCodes.REQUIRED + ":fundDetails"), errors.asMap()::toString);
    }

    @Test
    void osrRowIsRejectedForGovernmentGrantComponent() {
        ActionPlan plan = TestData.validPlan();
        projectComponents(plan).get(0).getFundDetails().get(0).setFundType("OSR");
        ValidationErrors errors = run(plan);
        assertTrue(errors.containsRule(ErrorCodes.FUND_SOURCE_CONFLICT), errors.asMap()::toString);
    }

    @Test
    void componentSourceMustFitPlanSource() {
        ActionPlan plan = TestData.validPlan();
        plan.setFundSource("GOVT_GRANT");
        ValidationErrors errors = run(plan);
        assertTrue(errors.asMap().containsKey(ErrorCodes.FUND_SOURCE_CONFLICT
                        + ":projectDetails[0].components[2].fundSource"),
                errors.asMap()::toString);
    }

    @Test
    void futureApprovalDateIsRejected() {
        ActionPlan plan = TestData.validPlan();
        plan.setOfflineApprovalDate(TODAY.plusDays(1).atStartOfDay(ZONE).toInstant().toEpochMilli());
        assertTrue(run(plan).asMap().containsKey(ErrorCodes.INVALID_DATE + ":offlineApprovalDate"));
    }

    @Test
    void approvalDateTodayIsAccepted() {
        ActionPlan plan = TestData.validPlan();
        plan.setOfflineApprovalDate(TODAY.atTime(23, 59).atZone(ZONE).toInstant().toEpochMilli());
        assertTrue(run(plan).isEmpty());
    }

    @Test
    void othersPurposeNeedsText() {
        ActionPlan plan = TestData.validPlan();
        plan.getProjectDetails().get(0).setPurposeCode("OTHERS");
        assertTrue(run(plan).asMap().containsKey(ErrorCodes.REQUIRED + ":projectDetails[0].purposeOther"));
    }

    @Test
    void mlaRecommendationNeedsConstituency() {
        ActionPlan plan = TestData.validPlan();
        plan.getLocations().get(0).setMlaConstituency(null);
        assertTrue(run(plan).asMap().containsKey(ErrorCodes.REQUIRED + ":locations[0].mlaConstituency"));
    }

    @Test
    void technicalSanctionDocumentIsRequiredWhenYes() {
        ActionPlan plan = TestData.validPlan();
        plan.getDocuments().removeIf(d -> d.getDocumentType().equals("TECHNICAL_SANCTION"));
        assertTrue(run(plan).asMap().containsKey(ErrorCodes.DOCUMENT_REQUIRED + ":documents.TECHNICAL_SANCTION"));
    }

    @Test
    void supportingDocumentLimitIsEnforced() {
        ActionPlan plan = TestData.validPlan();
        for (int i = 0; i < 6; i++) {
            plan.getDocuments().add(TestData.doc("REFERENCE"));
        }
        assertTrue(run(plan).containsRule(ErrorCodes.DOCUMENT_LIMIT));
    }

    @Test
    void zeroAmountAndCountMismatchAreReported() {
        ActionPlan plan = TestData.validPlan();
        plan.setTotalComponents(4);
        projectComponents(plan).get(0).setApprovedAmount(BigDecimal.ZERO);
        ValidationErrors errors = run(plan);
        assertTrue(errors.containsRule(ErrorCodes.COMPONENT_COUNT_MISMATCH));
        assertTrue(errors.asMap().containsKey(ErrorCodes.INVALID_AMOUNT
                + ":projectDetails[0].components[0].approvedAmount"));
    }

    @Test
    void inactiveComponentsAreIgnoredInTotals() {
        ActionPlan plan = TestData.validPlan();
        projectComponents(plan).add(TestData.component("C-9", "Old item", "GOODS", "GOVT_GRANT", "999",
                TestData.fund("GOVT_GRANT", "CENTRAL_GRANT", "999")));
        projectComponents(plan).get(3).setStatus("INACTIVE");
        assertTrue(run(plan).isEmpty());
    }

    @Test
    void sameComponentNameIsOnlyAWarning() {
        ActionPlan plan = TestData.validPlan();
        projectComponents(plan).get(1).setComponentName("  manpower   FOR drainage ");
        ValidationErrors errors = new ValidationErrors();
        List<ValidationWarning> warnings = new SubmissionRules(TestData.mdms(), ZONE, TODAY).validate(plan, errors);
        assertTrue(errors.isEmpty(), errors.asMap()::toString);
        assertEquals(1, warnings.size());
    }

    @Test
    void declarationIsOptionalAndMastersAreChecked() {
        ActionPlan plan = TestData.validPlan();
        plan.setDeclarationAccepted(false);
        plan.setSchemeCode("UNKNOWN");
        plan.getLocations().get(0).setDistrictCode("NOWHERE");
        ValidationErrors errors = run(plan);
        assertFalse(errors.containsRule(ErrorCodes.DECLARATION_REQUIRED));
        assertTrue(errors.asMap().containsKey(ErrorCodes.INVALID_MASTER + ":schemeCode"));
        assertTrue(errors.asMap().containsKey(ErrorCodes.INVALID_MASTER + ":locations[0].districtCode"));
        assertFalse(errors.asMap().containsKey(ErrorCodes.REQUIRED + ":subSchemeCode"));
    }
}
