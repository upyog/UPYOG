package org.upyog.wms.validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.upyog.wms.web.models.ActionPlan;
import org.upyog.wms.web.models.Component;
import org.upyog.wms.web.models.Document;
import org.upyog.wms.web.models.FundDetail;
import org.upyog.wms.web.models.Location;
import org.upyog.wms.web.models.ProjectDetail;
import org.upyog.wms.web.models.Recommendation;
import org.upyog.wms.web.models.mdms.ActionPlanConfig;
import org.upyog.wms.web.models.mdms.DocumentTypeConfig;
import org.upyog.wms.web.models.mdms.MdmsData;

/** A complete, valid plan (the FRS example: 100 = Manpower 20 + Material 40 + Service 40) and its masters. */
final class TestData {

    static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");
    static final LocalDate TODAY = LocalDate.of(2026, 11, 10);

    private TestData() {
    }

    static MdmsData mdms(ActionPlanConfig config) {
        return new MdmsData(
                Set.of("2025-26", "2026-27"),
                Set.of("DEVELOPMENT_WORK", "OTHERS"),
                Set.of("ORIGINAL", "REPAIRS"),
                Set.of("BUILDINGS", "BRIDGES"),
                Set.of("ULB_INITIATIVE", "MP", "MLA", "OTHERS"),
                Map.of("FINANCE_COMMISSION", Set.of("STREET_LIGHTS"), "MUNICIPAL_WORKS", Set.of("MUNICIPAL_ROAD_WORKS")),
                Set.of("CENTRAL_GRANT", "STATE_GOVT_GRANT"),
                Set.of("PROPERTY_TAX", "TRADE_LICENSE_FEE"),
                Map.of("MANPOWER", Set.of(), "GOODS", Set.of("FURNITURE"), "SERVICES", Set.of("CLOUD")),
                Map.of(
                        "ACTION_PLAN", new DocumentTypeConfig("ACTION_PLAN", "ACTION_PLAN", true, false),
                        "ADMIN_SANCTION", new DocumentTypeConfig("ADMIN_SANCTION", "ACTION_PLAN", true, false),
                        "TECHNICAL_SANCTION", new DocumentTypeConfig("TECHNICAL_SANCTION", "ACTION_PLAN", false, false),
                        "APPROVAL_PROCEEDING", new DocumentTypeConfig("APPROVAL_PROCEEDING", "ACTION_PLAN", false, true),
                        "REFERENCE", new DocumentTypeConfig("REFERENCE", "ACTION_PLAN", false, true),
                        "RECOMMENDATION", new DocumentTypeConfig("RECOMMENDATION", "RECOMMENDATION", false, false),
                        "COMPONENT_SUPPORTING", new DocumentTypeConfig("COMPONENT_SUPPORTING", "COMPONENT", false, false)),
                Set.of("UDD", "FINANCE"),
                Set.of("OD"),
                Map.of("CUTTACK", "OD", "KHORDHA", "OD"),
                config);
    }

    static MdmsData mdms() {
        return mdms(ActionPlanConfig.defaults());
    }

    static ActionPlan validPlan() {
        List<Component> components = new ArrayList<>(List.of(
                component("C-1", "Manpower for drainage", "MANPOWER", "GOVT_GRANT", "20",
                        fund("GOVT_GRANT", "CENTRAL_GRANT", "20")),
                component("C-2", "Building material", "GOODS", "GOVT_GRANT", "40",
                        fund("GOVT_GRANT", "CENTRAL_GRANT", "40")),
                component("C-3", "Supervision service", "SERVICES", "OSR", "40",
                        fund("OSR", "PROPERTY_TAX", "40"))));
        return ActionPlan.builder()
                .tenantId("od.cuttack")
                .financialYear("2026-27")
                .projectId("PRJ001")
                .projectName("Ring road drain")
                .adminSanction(true)
                .technicalSanction(true)
                .projectDetails(new ArrayList<>(List.of(ProjectDetail.builder()
                        .purposeCode("DEVELOPMENT_WORK")
                        .startDate(1767225600000L).endDate(1798761600000L)
                        .typeOfWork("ORIGINAL").workCategory("BUILDINGS").build())))
                .recommendations(new ArrayList<>(List.of(Recommendation.builder().recommendedBy("MLA").build())))
                .locations(new ArrayList<>(List.of(Location.builder().stateCode("OD").districtCode("CUTTACK")
                        .mlaConstituency("Cuttack Sadar").build())))
                .offlineApprovalDate(1762128000000L)
                .approvingAuthority("Commissioner")
                .schemeCode("FINANCE_COMMISSION").subSchemeCode("STREET_LIGHTS")
                .approvedAmount(new BigDecimal("100"))
                .fundSource("PARTIAL")
                .fundDetails(new ArrayList<>(List.of(fund("GOVT_GRANT", "CENTRAL_GRANT", "60"),
                        fund("OSR", "PROPERTY_TAX", "40"))))
                .totalComponents(3)
                .components(components)
                .documents(new ArrayList<>(List.of(doc("ACTION_PLAN"), doc("ADMIN_SANCTION"),
                        doc("TECHNICAL_SANCTION"))))
                .declarationAccepted(true)
                .build();
    }

    static Component component(String number, String name, String category, String source, String amount,
                               FundDetail fund) {
        return Component.builder().componentNumber(number).componentName(name).category(category)
                .fundSource(source).approvedAmount(new BigDecimal(amount)).status("ACTIVE")
                .fundDetails(new ArrayList<>(List.of(fund))).build();
    }

    static FundDetail fund(String type, String source, String amount) {
        return FundDetail.builder().fundType(type).sourceCode(source).fundName(source + " fund")
                .amount(new BigDecimal(amount)).build();
    }

    static Document doc(String type) {
        return Document.builder().documentType(type).fileStoreId("fs-" + type).build();
    }
}
