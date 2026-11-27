package org.upyog.wms.validator;

import static org.upyog.wms.constants.ErrorCodes.*;
import static org.upyog.wms.constants.WmsConstants.COMPONENT_INACTIVE;
import static org.upyog.wms.constants.WmsConstants.USER_TYPE_EMPLOYEE;
import static org.upyog.wms.util.WmsUtil.isBlank;
import static org.upyog.wms.util.WmsUtil.nn;
import static org.upyog.wms.util.WmsUtil.projectComponents;
import static org.upyog.wms.util.WmsUtil.projectDetails;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;
import org.upyog.wms.config.WmsConfiguration;
import org.upyog.wms.constants.ActionPlanAction;
import org.upyog.wms.constants.ActionPlanStatus;
import org.upyog.wms.constants.FundType;
import org.upyog.wms.util.WmsUtil;
import org.upyog.wms.web.models.ActionPlan;
import org.upyog.wms.web.models.ActionPlanRequest;
import org.upyog.wms.web.models.ActionPlanSearchCriteria;
import org.upyog.wms.web.models.ProjectDetail;
import org.upyog.wms.web.models.ValidationWarning;
import org.upyog.wms.web.models.Workflow;
import org.upyog.wms.web.models.mdms.MdmsData;

/**
 * Request level validation: who may do what, in which status, with which action. Data rules for a
 * submission are delegated to {@link SubmissionRules}.
 */
@Component
@RequiredArgsConstructor
public class ActionPlanValidator {

    private static final Set<String> SORT_FIELDS = Set.of("createdTime", "approvedDate", "projectId",
            "applicationNumber", "actionPlanNumber");

    private final WmsConfiguration config;

    /**
     * @param projectPlans every existing version registered with any requested projectId (new plan) or the same
     *                     actionPlanNumber (revision)
     */
    public void validateCreate(ActionPlanRequest request, List<ActionPlan> projectPlans) {
        ActionPlan plan = request.getActionPlan();
        validateUser(request.getRequestInfo(), plan.getTenantId());

        ValidationErrors errors = new ValidationErrors();
        String action = Optional.ofNullable(plan.getWorkflow()).map(Workflow::getAction).orElse(null);
        if (action != null && ActionPlanAction.from(action).filter(a -> a == ActionPlanAction.INITIATE).isEmpty()) {
            errors.add(INVALID_ACTION, "workflow.action", "Only INITIATE is allowed on _create");
        }
        draftMinimum(plan, errors);

        if (isBlank(plan.getActionPlanNumber())) {
            validateProjectsAvailable(plan, projectPlans, null, errors);
        } else {
            validateRevision(plan, projectPlans, errors);
        }
        errors.throwIfAny();
    }

    private void validateRevision(ActionPlan plan, List<ActionPlan> versions, ValidationErrors errors) {
        Optional<ActionPlan> published = versions.stream()
                .filter(v -> WmsUtil.isTrue(v.getIsLatestVersion())
                        && ActionPlanStatus.APPROVED.name().equals(v.getStatus()))
                .findFirst();
        if (published.isEmpty()) {
            errors.add(REVISION_NOT_ALLOWED, "actionPlanNumber", "No approved Action Plan found with number "
                    + plan.getActionPlanNumber());
            return;
        }
        versions.stream()
                .filter(v -> ActionPlanStatus.isInProgress(v.getStatus()))
                .findFirst()
                .ifPresent(v -> errors.add(REVISION_NOT_ALLOWED, "actionPlanNumber", "Revision "
                        + v.getApplicationNumber() + " is already in progress (" + v.getStatus() + ")"));
        Map<String, ProjectDetail> publishedProjects = projectsByProjectId(published.get());
        Set<String> incomingIds = projectDetails(plan).stream().map(ProjectDetail::getProjectId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        if (!incomingIds.equals(publishedProjects.keySet())) {
            errors.add(REVISION_NOT_ALLOWED, "projectDetails",
                    "Projects cannot be added, removed or changed in a revision");
        }
        // A carried component must belong to the same project in the published version.
        List<ProjectDetail> details = projectDetails(plan);
        for (int p = 0; p < details.size(); p++) {
            ProjectDetail publishedProject = publishedProjects.get(details.get(p).getProjectId());
            Set<String> known = publishedProject == null ? Set.of() : nn(publishedProject.getComponents()).stream()
                    .map(org.upyog.wms.web.models.Component::getComponentNumber).filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            List<org.upyog.wms.web.models.Component> components = nn(details.get(p).getComponents());
            for (int i = 0; i < components.size(); i++) {
                String number = components.get(i).getComponentNumber();
                if (number != null && !known.contains(number)) {
                    errors.add(REVISION_NOT_ALLOWED, "projectDetails[" + p + "].components[" + i
                                    + "].componentNumber", "Component " + number + " is not part of project "
                                    + details.get(p).getProjectId() + " in Action Plan " + plan.getActionPlanNumber()
                                    + "; leave the number empty for a new component");
                }
            }
        }
    }

    /**
     * @param sameProject other plans using any newly requested projectId
     * @return warnings from submission rules (empty for other actions)
     */
    public List<ValidationWarning> validateUpdate(ActionPlanRequest request, ActionPlan existing, MdmsData mdms,
                                                  boolean workflowEnabled, List<ActionPlan> sameProject) {
        ActionPlan plan = request.getActionPlan();
        RequestInfo requestInfo = request.getRequestInfo();
        if (existing == null) {
            throw new CustomException(NOT_FOUND, "Action Plan not found for id " + plan.getId());
        }
        validateUser(requestInfo, existing.getTenantId());

        ValidationErrors errors = new ValidationErrors();
        if (plan.getApplicationNumber() != null
                && !plan.getApplicationNumber().equals(existing.getApplicationNumber())) {
            errors.add(INVALID_ACTION, "applicationNumber", "Application number does not match the record");
        }
        String actionCode = Optional.ofNullable(plan.getWorkflow()).map(Workflow::getAction).orElse(null);
        Optional<ActionPlanAction> parsed = ActionPlanAction.from(actionCode)
                .filter(a -> a != ActionPlanAction.INITIATE);
        if (parsed.isEmpty()) {
            errors.add(INVALID_ACTION, "workflow.action", "A valid workflow action is required on _update");
            errors.throwIfAny();
        }
        ActionPlanAction action = parsed.get();
        ActionPlanStatus status = ActionPlanStatus.from(existing.getStatus()).orElse(null);
        String user = WmsUtil.userUuid(requestInfo);

        switch (action.kind()) {
            case EDIT, SUBMIT -> {
                if (status == null || !status.isEditable()) {
                    errors.add(NOT_EDITABLE, "status", "Action Plan in status " + existing.getStatus()
                            + " cannot be modified");
                }
                if (!Objects.equals(user, existing.getAuditDetails().getCreatedBy())) {
                    errors.add(UNAUTHORIZED, "auditDetails.createdBy", "Only the creator can edit or submit");
                }
                draftMinimum(plan, errors);
                validateProjectChanges(plan, existing, sameProject, errors);
                validateChildIds(plan, existing, errors);
            }
            case DECISION -> validateDecision(plan, existing, action, user, mdms, workflowEnabled, errors);
            case WITHDRAW -> {
                if (!Objects.equals(user, existing.getAuditDetails().getCreatedBy())) {
                    errors.add(UNAUTHORIZED, "auditDetails.createdBy", "Only the creator can withdraw");
                }
            }
            default -> errors.add(INVALID_ACTION, "workflow.action", "Action " + action + " is not allowed here");
        }

        if (action.isSubmission() && status != null) {
            boolean resubmit = action == ActionPlanAction.RESUBMIT || action == ActionPlanAction.RESUBMIT_TO_APPROVER;
            if (resubmit && status != ActionPlanStatus.RETURNED) {
                errors.add(INVALID_ACTION, "workflow.action", action + " is only for a returned plan; use SUBMIT");
            } else if (!resubmit && status == ActionPlanStatus.RETURNED) {
                errors.add(INVALID_ACTION, "workflow.action", "The plan was returned: use RESUBMIT");
            }
        }
        if (action.isSubmission()) {
            boolean toVerifier = action.routesToVerifier();
            if (workflowEnabled && mdms.config().verificationRequired() && !toVerifier) {
                errors.add(INVALID_ACTION, "workflow.action", "Verification is configured: use SUBMIT / RESUBMIT");
            } else if (workflowEnabled && !mdms.config().verificationRequired() && toVerifier) {
                errors.add(INVALID_ACTION, "workflow.action",
                        "Verification is not configured: use SUBMIT_TO_APPROVER / RESUBMIT_TO_APPROVER");
            }
        }
        // Stop here when the request itself is wrong; data rules make sense only for a valid request.
        errors.throwIfAny();

        List<ValidationWarning> warnings = List.of();
        if (action.isSubmission()) {
            LocalDate today = LocalDate.now(config.businessZone());
            warnings = new SubmissionRules(mdms, config.businessZone(), today).validate(plan, errors);
        }
        errors.throwIfAny();
        return warnings;
    }

    private void validateDecision(ActionPlan plan, ActionPlan existing, ActionPlanAction action, String user,
                                  MdmsData mdms, boolean workflowEnabled, ValidationErrors errors) {
        if (!workflowEnabled) {
            errors.add(INVALID_ACTION, "workflow.action", "Workflow is switched off; " + action
                    + " is not available (plans are approved on submission)");
            return;
        }
        Workflow wf = plan.getWorkflow();
        if (isBlank(wf.getComments())) {
            errors.add(REQUIRED, "workflow.comments", "Remarks are mandatory for " + action);
        }
        if (!isBlank(wf.getApprovingDepartment())
                && !mdms.approvingDepartments().contains(wf.getApprovingDepartment())) {
            errors.add(INVALID_MASTER, "workflow.approvingDepartment", "Approving Department is not valid");
        }
        if (mdms.config().makerCheckerSeparation()
                && Objects.equals(user, existing.getAuditDetails().getCreatedBy())) {
            errors.add(MAKER_CHECKER, "workflow.action", "The creator of the plan cannot verify or approve it");
        }
        // FR-AP-22: senior approval only for component-derived Government Grant / Partial funding.
        Optional<FundType> derivedSource = componentFundSource(existing);
        if (derivedSource.isEmpty()) {
            errors.add(INVALID_ACTION, "projectDetails.components.fundSource",
                    "Fund source cannot be determined from the active components");
            return;
        }
        String fundSource = derivedSource.get().name();
        boolean senior = mdms.config().requiresSeniorApproval(fundSource);
        if (action == ActionPlanAction.VERIFY && !senior) {
            errors.add(INVALID_ACTION, "workflow.action", "Fund source " + fundSource
                    + " does not need senior approval: use VERIFY_AND_APPROVE");
        } else if (action == ActionPlanAction.VERIFY_AND_APPROVE && senior) {
            errors.add(INVALID_ACTION, "workflow.action", "Fund source " + fundSource
                    + " needs senior approval: use VERIFY");
        }
    }

    private static Optional<FundType> componentFundSource(ActionPlan plan) {
        Set<FundType> sources = new HashSet<>();
        for (org.upyog.wms.web.models.Component component : projectComponents(plan)) {
            if (!WmsUtil.isLive(component.getIsActive()) || COMPONENT_INACTIVE.equals(component.getStatus())) {
                continue;
            }
            Optional<FundType> source = FundType.from(component.getFundSource());
            if (source.isEmpty()) {
                return Optional.empty();
            }
            sources.add(source.get());
        }
        if (sources.isEmpty()) {
            return Optional.empty();
        }
        return sources.contains(FundType.PARTIAL) || sources.size() > 1
                ? Optional.of(FundType.PARTIAL) : sources.stream().findFirst();
    }

    private void validateProjectChanges(ActionPlan plan, ActionPlan existing, List<ActionPlan> sameProject,
                                        ValidationErrors errors) {
        Set<String> oldIds = projectsByProjectId(existing).keySet();
        Set<String> newIds = projectDetails(plan).stream().map(ProjectDetail::getProjectId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        if (existing.getVersion() != null && existing.getVersion() > 1 && !newIds.equals(oldIds)) {
            errors.add(INVALID_ACTION, "projectDetails", "Projects cannot be added, removed or changed in a revision");
            return;
        }
        validateProjectsAvailable(plan, sameProject, existing.getId(), errors);
    }

    private static void validateProjectsAvailable(ActionPlan plan, List<ActionPlan> candidates, String ownPlanId,
                                                  ValidationErrors errors) {
        List<ProjectDetail> requested = projectDetails(plan);
        for (int i = 0; i < requested.size(); i++) {
            int projectIndex = i;
            String projectId = requested.get(i).getProjectId();
            if (isBlank(projectId)) {
                continue;
            }
            candidates.stream()
                    .filter(candidate -> !Objects.equals(ownPlanId, candidate.getId()))
                    .filter(candidate -> projectsByProjectId(candidate).containsKey(projectId))
                    .findFirst()
                    .ifPresent(existing -> errors.add(PROJECT_ALREADY_REGISTERED,
                            "projectDetails[" + projectIndex + "].projectId", "Project ID " + projectId
                                    + " is already registered (application " + existing.getApplicationNumber()
                                    + ", status " + existing.getStatus() + ")"));
        }
    }

    private static Map<String, ProjectDetail> projectsByProjectId(ActionPlan plan) {
        return projectDetails(plan).stream().filter(p -> !isBlank(p.getProjectId()))
                .collect(Collectors.toMap(ProjectDetail::getProjectId, Function.identity(), (a, b) -> a,
                        java.util.LinkedHashMap::new));
    }

    /** Every child id sent by the client must belong to this plan (protects against cross-plan updates). */
    private void validateChildIds(ActionPlan plan, ActionPlan existing, ValidationErrors errors) {
        checkIds(plan.getProjectDetails(), existing.getProjectDetails(), p -> p.getId(), "projectDetails", errors);
        checkIds(plan.getLocations(), existing.getLocations(), l -> l.getId(), "locations", errors);
        checkIds(plan.getRecommendations(), existing.getRecommendations(), r -> r.getId(), "recommendations", errors);
        checkIds(plan.getFundDetails(), existing.getFundDetails(), f -> f.getId(), "fundDetails", errors);
        checkIds(plan.getBeneficiaries(), existing.getBeneficiaries(), b -> b.getId(), "beneficiaries", errors);
        checkIds(plan.getDocuments(), existing.getDocuments(), d -> d.getId(), "documents", errors);
        Map<String, org.upyog.wms.web.models.Component> existingComponents = projectComponents(existing).stream()
                .collect(Collectors.toMap(org.upyog.wms.web.models.Component::getId, Function.identity()));
        Map<String, ProjectDetail> existingProjects = nn(existing.getProjectDetails()).stream()
                .filter(p -> p.getId() != null).collect(Collectors.toMap(ProjectDetail::getId, Function.identity()));
        Map<String, org.upyog.wms.web.models.Recommendation> existingRecs = nn(existing.getRecommendations())
                .stream().collect(Collectors.toMap(r -> r.getId(), Function.identity()));
        List<ProjectDetail> details = nn(plan.getProjectDetails());
        for (int projectIndex = 0; projectIndex < details.size(); projectIndex++) {
            List<org.upyog.wms.web.models.Component> components = nn(details.get(projectIndex).getComponents());
            ProjectDetail oldProject = details.get(projectIndex).getId() == null ? null
                    : existingProjects.get(details.get(projectIndex).getId());
            checkIds(components, oldProject == null ? null : oldProject.getComponents(),
                    org.upyog.wms.web.models.Component::getId,
                    "projectDetails[" + projectIndex + "].components", errors);
            for (int i = 0; i < components.size(); i++) {
                var c = components.get(i);
                var old = c.getId() == null ? null : existingComponents.get(c.getId());
                String p = "projectDetails[" + projectIndex + "].components[" + i + "].";
                checkIds(c.getFundDetails(), old == null ? null : old.getFundDetails(), f -> f.getId(), p + "fundDetails", errors);
                checkIds(c.getBeneficiaries(), old == null ? null : old.getBeneficiaries(), b -> b.getId(), p + "beneficiaries", errors);
                checkIds(c.getDocuments(), old == null ? null : old.getDocuments(), d -> d.getId(), p + "documents", errors);
            }
        }
        List<org.upyog.wms.web.models.Recommendation> recs = nn(plan.getRecommendations());
        for (int i = 0; i < recs.size(); i++) {
            var r = recs.get(i);
            var old = r.getId() == null ? null : existingRecs.get(r.getId());
            checkIds(r.getDocuments(), old == null ? null : old.getDocuments(), d -> d.getId(),
                    "recommendations[" + i + "].documents", errors);
        }
    }

    private static <T> void checkIds(List<T> incoming, List<T> existing, Function<T, String> id, String path,
                                     ValidationErrors errors) {
        Set<String> known = nn(existing).stream().map(id).collect(Collectors.toSet());
        Set<String> seen = new HashSet<>();
        List<T> items = nn(incoming);
        for (int i = 0; i < items.size(); i++) {
            String value = id.apply(items.get(i));
            if (value == null) {
                continue;
            }
            if (!known.contains(value)) {
                errors.add(INVALID_ACTION, path + "[" + i + "].id", "Id " + value + " does not belong to this plan");
            } else if (!seen.add(value)) {
                errors.add(DUPLICATE, path + "[" + i + "].id", "Id " + value + " is sent more than once");
            }
        }
    }

    public void validateSearch(RequestInfo requestInfo, ActionPlanSearchCriteria criteria) {
        validateUser(requestInfo, criteria.getTenantId());
        ValidationErrors errors = new ValidationErrors();
        if (criteria.getSortBy() != null && !SORT_FIELDS.contains(criteria.getSortBy())) {
            errors.add(INVALID_SEARCH, "sortBy", "sortBy must be one of " + SORT_FIELDS);
        }
        if (criteria.getSortOrder() != null && !Set.of("ASC", "DESC").contains(criteria.getSortOrder().toUpperCase())) {
            errors.add(INVALID_SEARCH, "sortOrder", "sortOrder must be ASC or DESC");
        }
        if (criteria.getFromDate() != null && criteria.getToDate() != null
                && criteria.getFromDate() > criteria.getToDate()) {
            errors.add(INVALID_SEARCH, "fromDate", "fromDate must not be after toDate");
        }
        if (criteria.getLimit() != null && criteria.getLimit() > config.getMaxLimit()) {
            errors.add(INVALID_SEARCH, "limit", "limit must not exceed " + config.getMaxLimit());
        }
        errors.throwIfAny();
    }

    /** Draft rules (FR-AP-04): only what the database needs to store a draft. */
    private void draftMinimum(ActionPlan plan, ValidationErrors errors) {
        if (isBlank(plan.getFinancialYear())) {
            errors.add(REQUIRED, "financialYear", "Financial Year is mandatory even for a draft");
        }
        List<ProjectDetail> details = projectDetails(plan);
        if (details.isEmpty()) {
            errors.add(REQUIRED, "projectDetails", "At least one active project detail is mandatory even for a draft");
            return;
        }
        Set<String> projectIds = new HashSet<>();
        for (int i = 0; i < details.size(); i++) {
            ProjectDetail detail = details.get(i);
            String path = "projectDetails[" + i + "].";
            if (isBlank(detail.getProjectId())) {
                errors.add(REQUIRED, path + "projectId", "Project ID is mandatory even for a draft");
            } else if (!projectIds.add(detail.getProjectId())) {
                errors.add(DUPLICATE, path + "projectId", "Project ID " + detail.getProjectId()
                        + " is repeated in this Action Plan");
            }
            if (isBlank(detail.getProjectName())) {
                errors.add(REQUIRED, path + "projectName", "Project Name is mandatory even for a draft");
            }
        }
    }

    /** Authenticated employee with a role in the plan's ULB (or at state level). */
    private void validateUser(RequestInfo requestInfo, String tenantId) {
        if (isBlank(tenantId)) {
            throw new CustomException(REQUIRED, "tenantId is mandatory");
        }
        User user = requestInfo == null ? null : requestInfo.getUserInfo();
        if (user == null || isBlank(user.getUuid())) {
            throw new CustomException(UNAUTHORIZED, "RequestInfo.userInfo with uuid is required");
        }
        if (!USER_TYPE_EMPLOYEE.equalsIgnoreCase(user.getType())) {
            throw new CustomException(UNAUTHORIZED, "Only employees can access Action Plans");
        }
        String stateTenant = WmsUtil.stateLevelTenant(tenantId);
        boolean hasRole = nn(user.getRoles()).stream()
                .map(Role::getTenantId)
                .anyMatch(t -> tenantId.equals(t) || stateTenant.equals(t)
                        || (t != null && t.startsWith(tenantId + ".")));
        if (!hasRole) {
            throw new CustomException(UNAUTHORIZED, "User has no role in tenant " + tenantId);
        }
    }
}
