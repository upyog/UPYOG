package org.upyog.wms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.junit.jupiter.api.Test;
import org.upyog.wms.config.WmsConfiguration;
import org.upyog.wms.web.models.ActionPlan;
import org.upyog.wms.web.models.ActionPlanRequest;
import org.upyog.wms.web.models.AuditDetails;
import org.upyog.wms.web.models.Component;
import org.upyog.wms.web.models.FundDetail;
import org.upyog.wms.web.models.ProjectDetail;

class EnrichmentServiceTest {

    /** IdGen replaced by a counter. */
    private static final class FakeIdGen extends IdGenService {
        private final AtomicInteger next = new AtomicInteger();

        FakeIdGen() {
            super(null, null);
        }

        @Override
        public List<String> generate(RequestInfo requestInfo, String tenantId, String idName, int count) {
            return IntStream.range(0, count).mapToObj(i -> "ID-" + next.incrementAndGet()).toList();
        }
    }

    private final EnrichmentService service = new EnrichmentService(new FakeIdGen(), new WmsConfiguration());

    private static RequestInfo requestInfo() {
        User user = new User();
        user.setUuid("user-1");
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setUserInfo(user);
        return requestInfo;
    }

    private static Component component(String name) {
        return Component.builder().componentName(name).approvedAmount(BigDecimal.TEN)
                .fundDetails(new ArrayList<>(List.of(FundDetail.builder().fundType("OSR").amount(BigDecimal.TEN).build())))
                .build();
    }

    private ActionPlan created() {
        ActionPlan plan = ActionPlan.builder().tenantId("od.cuttack").projectId("P1").projectName("Drain")
                .financialYear("2026-27")
                .projectDetails(new ArrayList<>(List.of(ProjectDetail.builder().purposeCode("DEVELOPMENT_WORK").build())))
                .components(new ArrayList<>(List.of(component("A"), component("B"))))
                .build();
        ActionPlanRequest request = ActionPlanRequest.builder().requestInfo(requestInfo()).actionPlan(plan).build();
        service.enrichCreate(request, null, 1);
        return plan;
    }

    @Test
    void createFillsIdsNumbersAndParentLinks() {
        ActionPlan plan = created();
        assertEquals("ID-1", plan.getApplicationNumber());
        assertEquals(1, plan.getVersion());
        assertTrue(plan.getIsLatestVersion());
        assertEquals("INITIATE", plan.getWorkflow().getAction());
        assertEquals(List.of("ID-2", "ID-3"), plan.getComponents().stream().map(Component::getComponentNumber).toList());
        Component a = plan.getComponents().get(0);
        assertEquals(a.getId(), a.getFundDetails().get(0).getComponentId());
        assertEquals(plan.getId(), a.getFundDetails().get(0).getActionPlanId());
        assertEquals("ACTIVE", a.getStatus());
        assertTrue(plan.getDocuments().isEmpty());
        assertEquals("user-1", plan.getAuditDetails().getCreatedBy());
    }

    @Test
    void updateSoftDeletesRemovedRowsAndKeepsBusinessIds() {
        ActionPlan existing = created();
        existing.setStatus("INITIATED");
        String keptId = existing.getComponents().get(0).getId();
        String droppedId = existing.getComponents().get(1).getId();
        String detailId = existing.getProjectDetails().get(0).getId();

        Component kept = component("A renamed");
        kept.setId(keptId);
        kept.setComponentNumber("SPOOFED");
        ActionPlan edit = ActionPlan.builder().id(existing.getId()).tenantId("od.cuttack").projectId("P1")
                .projectName("Drain").financialYear("2026-27")
                .projectDetails(new ArrayList<>(List.of(ProjectDetail.builder().purposeCode("OTHERS").build())))
                .components(new ArrayList<>(List.of(kept, component("C"))))
                .build();
        ActionPlanRequest request = ActionPlanRequest.builder().requestInfo(requestInfo()).actionPlan(edit).build();
        service.enrichUpdate(request, existing);

        List<Component> result = edit.getComponents();
        assertEquals(3, result.size());
        assertEquals(droppedId, result.get(0).getId());
        assertFalse(result.get(0).getIsActive());
        assertEquals("ID-2", result.get(1).getComponentNumber());
        assertEquals("ID-4", result.get(2).getComponentNumber());
        assertEquals(detailId, edit.getProjectDetails().get(0).getId());
        assertEquals(1, edit.getProjectDetails().size());
        assertEquals("INITIATED", edit.getStatus());
        AuditDetails audit = edit.getAuditDetails();
        assertEquals(existing.getAuditDetails().getCreatedTime(), audit.getCreatedTime());
    }

    @Test
    void revisionCarriesRemovedComponentsAsInactive() {
        ActionPlan published = created();
        published.setActionPlanNumber("APN-1");
        Component keep = component("A");
        keep.setComponentNumber(published.getComponents().get(0).getComponentNumber());
        ActionPlan revision = ActionPlan.builder().tenantId("od.cuttack").actionPlanNumber("APN-1").projectName("Drain")
                .financialYear("2026-27").components(new ArrayList<>(List.of(keep))).build();
        service.enrichCreate(ActionPlanRequest.builder().requestInfo(requestInfo()).actionPlan(revision).build(),
                published, 2);

        assertEquals(2, revision.getVersion());
        assertFalse(revision.getIsLatestVersion());
        assertEquals("P1", revision.getProjectId());
        assertEquals(2, revision.getComponents().size());
        Component carried = revision.getComponents().get(1);
        assertEquals("INACTIVE", carried.getStatus());
        assertEquals(published.getComponents().get(1).getComponentNumber(), carried.getComponentNumber());
    }
}
