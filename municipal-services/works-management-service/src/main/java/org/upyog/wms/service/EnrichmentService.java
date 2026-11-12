package org.upyog.wms.service;

import static org.upyog.wms.constants.WmsConstants.COMPONENT_ACTIVE;
import static org.upyog.wms.constants.WmsConstants.COMPONENT_INACTIVE;
import static org.upyog.wms.util.WmsUtil.isTrue;
import static org.upyog.wms.util.WmsUtil.nn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Service;
import org.upyog.wms.config.WmsConfiguration;
import org.upyog.wms.constants.ActionPlanAction;
import org.upyog.wms.util.WmsUtil;
import org.upyog.wms.web.models.ActionPlan;
import org.upyog.wms.web.models.ActionPlanRequest;
import org.upyog.wms.web.models.AuditDetails;
import org.upyog.wms.web.models.Beneficiary;
import org.upyog.wms.web.models.Component;
import org.upyog.wms.web.models.Document;
import org.upyog.wms.web.models.FundDetail;
import org.upyog.wms.web.models.Location;
import org.upyog.wms.web.models.ProjectDetail;
import org.upyog.wms.web.models.Recommendation;
import org.upyog.wms.web.models.Workflow;

/**
 * Fills system values: ids, numbers, tenant, audit, parent links and soft-deletes.
 * Child lists are never null after enrichment, so egov-persister always receives arrays.
 */
@Service
@RequiredArgsConstructor
public class EnrichmentService {

    private final IdGenService idGenService;
    private final WmsConfiguration config;

    /** Uniform access to the common columns of every child type. */
    private record Ops<T>(Function<T, String> id, BiConsumer<T, String> setId,
                          Function<T, Boolean> active, BiConsumer<T, Boolean> setActive,
                          Function<T, AuditDetails> audit, BiConsumer<T, AuditDetails> setAudit,
                          BiConsumer<T, String> setTenant) {
    }

    private record Ctx(String tenantId, String user, long now) {
        AuditDetails fresh() {
            return AuditDetails.builder().createdBy(user).createdTime(now).lastModifiedBy(user)
                    .lastModifiedTime(now).build();
        }

        AuditDetails touched(AuditDetails old) {
            return AuditDetails.builder()
                    .createdBy(old == null ? user : old.getCreatedBy())
                    .createdTime(old == null ? now : old.getCreatedTime())
                    .lastModifiedBy(user).lastModifiedTime(now).build();
        }
    }

    private static final Ops<ProjectDetail> PROJECT_DETAIL = new Ops<>(ProjectDetail::getId, ProjectDetail::setId,
            ProjectDetail::getIsActive, ProjectDetail::setIsActive, ProjectDetail::getAuditDetails,
            ProjectDetail::setAuditDetails, ProjectDetail::setTenantId);
    private static final Ops<Location> LOCATION = new Ops<>(Location::getId, Location::setId,
            Location::getIsActive, Location::setIsActive, Location::getAuditDetails, Location::setAuditDetails,
            Location::setTenantId);
    private static final Ops<Recommendation> RECOMMENDATION = new Ops<>(Recommendation::getId,
            Recommendation::setId, Recommendation::getIsActive, Recommendation::setIsActive,
            Recommendation::getAuditDetails, Recommendation::setAuditDetails, Recommendation::setTenantId);
    private static final Ops<Component> COMPONENT = new Ops<>(Component::getId, Component::setId,
            Component::getIsActive, Component::setIsActive, Component::getAuditDetails,
            Component::setAuditDetails, Component::setTenantId);
    private static final Ops<FundDetail> FUND = new Ops<>(FundDetail::getId, FundDetail::setId,
            FundDetail::getIsActive, FundDetail::setIsActive, FundDetail::getAuditDetails,
            FundDetail::setAuditDetails, FundDetail::setTenantId);
    private static final Ops<Beneficiary> BENEFICIARY = new Ops<>(Beneficiary::getId, Beneficiary::setId,
            Beneficiary::getIsActive, Beneficiary::setIsActive, Beneficiary::getAuditDetails,
            Beneficiary::setAuditDetails, Beneficiary::setTenantId);
    private static final Ops<Document> DOCUMENT = new Ops<>(Document::getId, Document::setId,
            Document::getIsActive, Document::setIsActive, Document::getAuditDetails, Document::setAuditDetails,
            Document::setTenantId);

    /**
     * @param published the approved, latest version when this is a revision, otherwise null
     */
    public void enrichCreate(ActionPlanRequest request, ActionPlan published, int nextVersion) {
        ActionPlan plan = request.getActionPlan();
        RequestInfo requestInfo = request.getRequestInfo();
        Ctx ctx = new Ctx(plan.getTenantId(), WmsUtil.userUuid(requestInfo), System.currentTimeMillis());

        plan.setId(UUID.randomUUID().toString());
        plan.setApplicationNumber(idGenService.generateOne(requestInfo, plan.getTenantId(),
                config.getApplicationNumberIdName()));
        plan.setStatus(null);
        plan.setSubmittedDate(null);
        plan.setApprovedDate(null);
        plan.setApprovedBy(null);
        plan.setIsActive(true);
        plan.setAdminSanction(isTrue(plan.getAdminSanction()));
        plan.setDeclarationAccepted(isTrue(plan.getDeclarationAccepted()));
        plan.setAuditDetails(ctx.fresh());

        if (published != null) {
            // Revision: same project and action plan number, next version; becomes "latest" only when approved
            plan.setVersion(nextVersion);
            plan.setIsLatestVersion(false);
            plan.setActionPlanNumber(published.getActionPlanNumber());
            plan.setProjectId(published.getProjectId());
        } else {
            plan.setVersion(1);
            plan.setIsLatestVersion(true);
            plan.setActionPlanNumber(null);
            nn(plan.getComponents()).forEach(c -> c.setComponentNumber(null));
        }

        // Every row of a new version is a new row: ignore ids sent by the client
        plan.setProjectDetails(fresh(plan.getProjectDetails(), PROJECT_DETAIL, ctx));
        plan.setLocations(fresh(plan.getLocations(), LOCATION, ctx));
        plan.setRecommendations(fresh(plan.getRecommendations(), RECOMMENDATION, ctx));
        plan.getRecommendations().forEach(r -> r.setDocuments(fresh(r.getDocuments(), DOCUMENT, ctx)));
        plan.setFundDetails(fresh(plan.getFundDetails(), FUND, ctx));
        plan.setBeneficiaries(fresh(plan.getBeneficiaries(), BENEFICIARY, ctx));
        plan.setDocuments(fresh(plan.getDocuments(), DOCUMENT, ctx));
        plan.setComponents(fresh(plan.getComponents(), COMPONENT, ctx));
        plan.getComponents().forEach(c -> freshComponentChildren(c, ctx));

        if (published != null) {
            carryRemovedComponents(plan, published, ctx);
        }
        generateComponentNumbers(requestInfo, plan);
        linkParents(plan);

        Workflow workflow = plan.getWorkflow() == null ? new Workflow() : plan.getWorkflow();
        workflow.setAction(ActionPlanAction.INITIATE.name());
        plan.setWorkflow(workflow);
    }

    /** FR-AP-10: a component dropped in a revision stays in history, marked INACTIVE (never deleted). */
    private void carryRemovedComponents(ActionPlan plan, ActionPlan published, Ctx ctx) {
        Set<String> kept = plan.getComponents().stream().map(Component::getComponentNumber)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        nn(published.getComponents()).stream()
                .filter(c -> COMPONENT_ACTIVE.equals(c.getStatus()) && !kept.contains(c.getComponentNumber()))
                .map(c -> Component.builder()
                        .id(UUID.randomUUID().toString())
                        .tenantId(ctx.tenantId())
                        .componentNumber(c.getComponentNumber())
                        .componentName(c.getComponentName())
                        .category(c.getCategory())
                        .subCategory(c.getSubCategory())
                        .fundSource(c.getFundSource())
                        .approvedAmount(c.getApprovedAmount())
                        .status(COMPONENT_INACTIVE)
                        .isActive(true)
                        .fundDetails(new ArrayList<>())
                        .beneficiaries(new ArrayList<>())
                        .documents(new ArrayList<>())
                        .auditDetails(ctx.fresh())
                        .build())
                .forEach(plan.getComponents()::add);
    }

    public void enrichUpdate(ActionPlanRequest request, ActionPlan existing) {
        ActionPlan plan = request.getActionPlan();
        Ctx ctx = new Ctx(existing.getTenantId(), WmsUtil.userUuid(request.getRequestInfo()),
                System.currentTimeMillis());

        copySystemFields(existing, plan, ctx);
        if (existing.getVersion() != null && existing.getVersion() > 1) {
            plan.setProjectId(existing.getProjectId());
        }
        plan.setAdminSanction(isTrue(plan.getAdminSanction()));
        plan.setDeclarationAccepted(isTrue(plan.getDeclarationAccepted()));

        // 1:1 sections: reuse the existing row id so the partial unique index is never violated
        plan.setProjectDetails(merge(reuseIds(plan.getProjectDetails(), existing.getProjectDetails(),
                PROJECT_DETAIL), existing.getProjectDetails(), PROJECT_DETAIL, ctx));
        plan.setLocations(merge(reuseIds(plan.getLocations(), existing.getLocations(), LOCATION),
                existing.getLocations(), LOCATION, ctx));

        Map<String, Recommendation> oldRecs = byId(existing.getRecommendations(), Recommendation::getId);
        plan.setRecommendations(merge(plan.getRecommendations(), existing.getRecommendations(), RECOMMENDATION, ctx));
        plan.getRecommendations().forEach(r -> {
            Recommendation old = oldRecs.get(r.getId());
            if (WmsUtil.isLive(r.getIsActive())) {
                r.setDocuments(old == null ? fresh(r.getDocuments(), DOCUMENT, ctx)
                        : merge(r.getDocuments(), old.getDocuments(), DOCUMENT, ctx));
            } else {
                r.setDocuments(new ArrayList<>());
            }
        });

        plan.setFundDetails(merge(plan.getFundDetails(), existing.getFundDetails(), FUND, ctx));
        plan.setBeneficiaries(merge(plan.getBeneficiaries(), existing.getBeneficiaries(), BENEFICIARY, ctx));
        plan.setDocuments(merge(plan.getDocuments(), existing.getDocuments(), DOCUMENT, ctx));

        Map<String, Component> oldComps = byId(existing.getComponents(), Component::getId);
        plan.setComponents(merge(plan.getComponents(), existing.getComponents(), COMPONENT, ctx));
        for (Component c : plan.getComponents()) {
            Component old = oldComps.get(c.getId());
            if (old != null) {
                c.setComponentNumber(old.getComponentNumber());   // business id never changes
            }
            if (c.getStatus() == null) {
                c.setStatus(old == null ? COMPONENT_ACTIVE : old.getStatus());
            }
            if (!WmsUtil.isLive(c.getIsActive())) {
                c.setFundDetails(new ArrayList<>());
                c.setBeneficiaries(new ArrayList<>());
                c.setDocuments(new ArrayList<>());
            } else if (old == null) {
                freshComponentChildren(c, ctx);
            } else {
                c.setFundDetails(merge(c.getFundDetails(), old.getFundDetails(), FUND, ctx));
                c.setBeneficiaries(merge(c.getBeneficiaries(), old.getBeneficiaries(), BENEFICIARY, ctx));
                c.setDocuments(merge(c.getDocuments(), old.getDocuments(), DOCUMENT, ctx));
            }
        }
        generateComponentNumbers(request.getRequestInfo(), plan);
        linkParents(plan);
    }

    /**
     * Decision / withdrawal: plan data is locked (FR-AP-21), so the stored record is used and only the
     * workflow input of the request is kept.
     */
    public ActionPlan enrichStatusOnly(ActionPlanRequest request, ActionPlan existing) {
        Workflow workflow = request.getActionPlan().getWorkflow();
        existing.setWorkflow(workflow);
        existing.setAuditDetails(new Ctx(existing.getTenantId(), WmsUtil.userUuid(request.getRequestInfo()),
                System.currentTimeMillis()).touched(existing.getAuditDetails()));
        request.setActionPlan(existing);
        return existing;
    }

    private static void copySystemFields(ActionPlan from, ActionPlan to, Ctx ctx) {
        to.setId(from.getId());
        to.setTenantId(from.getTenantId());
        to.setApplicationNumber(from.getApplicationNumber());
        to.setActionPlanNumber(from.getActionPlanNumber());
        to.setVersion(from.getVersion());
        to.setIsLatestVersion(from.getIsLatestVersion());
        to.setStatus(from.getStatus());
        to.setSubmittedDate(from.getSubmittedDate());
        to.setApprovedDate(from.getApprovedDate());
        to.setApprovedBy(from.getApprovedBy());
        to.setIsActive(true);
        to.setAuditDetails(ctx.touched(from.getAuditDetails()));
    }

    private void freshComponentChildren(Component c, Ctx ctx) {
        c.setFundDetails(fresh(c.getFundDetails(), FUND, ctx));
        c.setBeneficiaries(fresh(c.getBeneficiaries(), BENEFICIARY, ctx));
        c.setDocuments(fresh(c.getDocuments(), DOCUMENT, ctx));
        if (c.getStatus() == null) {
            c.setStatus(COMPONENT_ACTIVE);
        }
    }

    /** One idgen call for all components that do not have a number yet (kept in entry order). */
    private void generateComponentNumbers(RequestInfo requestInfo, ActionPlan plan) {
        List<Component> missing = plan.getComponents().stream()
                .filter(c -> c.getComponentNumber() == null)
                .toList();
        Iterator<String> numbers = idGenService.generate(requestInfo, plan.getTenantId(),
                config.getComponentNumberIdName(), missing.size()).iterator();
        missing.forEach(c -> c.setComponentNumber(numbers.next()));
    }

    /** Sets tenant and parent ids on every child row. */
    private static void linkParents(ActionPlan plan) {
        plan.getFundDetails().forEach(f -> {
            f.setActionPlanId(plan.getId());
            f.setComponentId(null);
        });
        plan.getBeneficiaries().forEach(b -> {
            b.setActionPlanId(plan.getId());
            b.setComponentId(null);
        });
        plan.getComponents().forEach(c -> {
            c.getFundDetails().forEach(f -> {
                f.setActionPlanId(plan.getId());
                f.setComponentId(c.getId());
            });
            c.getBeneficiaries().forEach(b -> {
                b.setActionPlanId(plan.getId());
                b.setComponentId(c.getId());
            });
        });
    }

    private static <T> List<T> fresh(List<T> items, Ops<T> ops, Ctx ctx) {
        List<T> result = new ArrayList<>();
        for (T item : nn(items)) {
            if (!WmsUtil.isLive(ops.active().apply(item))) {
                continue;       // nothing to soft-delete in a brand new version
            }
            ops.setId().accept(item, UUID.randomUUID().toString());
            ops.setActive().accept(item, true);
            ops.setAudit().accept(item, ctx.fresh());
            ops.setTenant().accept(item, ctx.tenantId());
            result.add(item);
        }
        return result;
    }

    /**
     * Incoming rows keep their id (update) or get a new one (insert); stored rows missing from the request
     * are soft-deleted (isActive = false). Deactivated rows are placed first so the persister frees a unique
     * slot before a new row takes it.
     */
    private static <T> List<T> merge(List<T> incoming, List<T> existing, Ops<T> ops, Ctx ctx) {
        Map<String, T> stored = byId(existing, ops.id());
        Set<String> sent = new HashSet<>();
        List<T> current = new ArrayList<>();
        for (T item : nn(incoming)) {
            String id = ops.id().apply(item);
            T old = id == null ? null : stored.get(id);
            if (old == null && !WmsUtil.isLive(ops.active().apply(item))) {
                continue;       // a new row that is already removed: nothing to store
            }
            if (old == null) {
                ops.setId().accept(item, UUID.randomUUID().toString());
                ops.setAudit().accept(item, ctx.fresh());
            } else {
                sent.add(id);
                ops.setAudit().accept(item, ctx.touched(ops.audit().apply(old)));
            }
            if (ops.active().apply(item) == null) {
                ops.setActive().accept(item, true);
            }
            ops.setTenant().accept(item, ctx.tenantId());
            current.add(item);
        }
        List<T> result = new ArrayList<>();
        stored.forEach((id, old) -> {
            if (!sent.contains(id)) {
                ops.setActive().accept(old, false);
                ops.setAudit().accept(old, ctx.touched(ops.audit().apply(old)));
                result.add(old);
            }
        });
        current.sort(Comparator.comparing(t -> WmsUtil.isLive(ops.active().apply(t))));   // inactive first
        result.addAll(current);
        return result;
    }

    /** For 1:1 sections: give id-less incoming rows the id of a stored row that the request does not use. */
    private static <T> List<T> reuseIds(List<T> incoming, List<T> existing, Ops<T> ops) {
        Set<String> used = nn(incoming).stream().map(ops.id()).filter(Objects::nonNull).collect(Collectors.toSet());
        Iterator<String> free = nn(existing).stream().map(ops.id()).filter(id -> !used.contains(id)).iterator();
        nn(incoming).stream().filter(t -> ops.id().apply(t) == null && free.hasNext())
                .forEach(t -> ops.setId().accept(t, free.next()));
        return incoming;
    }

    private static <T> Map<String, T> byId(List<T> items, Function<T, String> id) {
        return nn(items).stream().collect(Collectors.toMap(id, Function.identity(), (a, b) -> a,
                java.util.LinkedHashMap::new));
    }
}
