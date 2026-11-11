package org.upyog.wms.service;

import static org.upyog.wms.util.WmsUtil.isBlank;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.upyog.wms.config.WmsConfiguration;
import org.upyog.wms.constants.ActionPlanAction;
import org.upyog.wms.constants.ActionPlanStatus;
import org.upyog.wms.constants.ErrorCodes;
import org.upyog.wms.kafka.Producer;
import org.upyog.wms.repository.ActionPlanRepository;
import org.upyog.wms.repository.ActionPlanRepository.SearchResult;
import org.upyog.wms.util.WmsUtil;
import org.upyog.wms.validator.ActionPlanValidator;
import org.upyog.wms.web.models.ActionPlan;
import org.upyog.wms.web.models.ActionPlanRequest;
import org.upyog.wms.web.models.ActionPlanSearchCriteria;
import org.upyog.wms.web.models.ValidationWarning;
import org.upyog.wms.web.models.mdms.MdmsData;

/**
 * Orchestrates create, update (draft edit, submission, decisions, withdrawal) and search.
 * Order on every write: validate -> enrich -> workflow transition -> publish to Kafka (egov-persister).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActionPlanService {

    private final ActionPlanValidator validator;
    private final EnrichmentService enrichmentService;
    private final WorkflowService workflowService;
    private final MdmsService mdmsService;
    private final IdGenService idGenService;
    private final CalculationService calculationService;
    private final ActionPlanRepository repository;
    private final Producer producer;
    private final WmsConfiguration config;

    public record UpdateResult(ActionPlan actionPlan, List<ValidationWarning> warnings) {
    }

    public ActionPlan create(ActionPlanRequest request) {
        ActionPlan plan = request.getActionPlan();
        RequestInfo requestInfo = request.getRequestInfo();
        boolean revision = !isBlank(plan.getActionPlanNumber());

        List<ActionPlan> related = isBlank(plan.getTenantId()) ? List.of() : revision
                ? find(ActionPlanSearchCriteria.builder().tenantId(plan.getTenantId())
                        .actionPlanNumbers(List.of(plan.getActionPlanNumber())).build(), true)
                : projectIdInUse(plan.getTenantId(), plan.getProjectId());
        validator.validateCreate(request, related);

        MdmsData mdms = mdmsService.getMasterData(requestInfo, plan.getTenantId());
        boolean workflowEnabled = workflowService.isWorkflowEnabled(mdms);

        ActionPlan published = revision ? related.stream()
                .filter(p -> WmsUtil.isTrue(p.getIsLatestVersion())
                        && ActionPlanStatus.APPROVED.name().equals(p.getStatus()))
                .findFirst().orElseThrow() : null;
        int nextVersion = related.stream().map(ActionPlan::getVersion).filter(Objects::nonNull)
                .max(Comparator.naturalOrder()).orElse(0) + 1;

        enrichmentService.enrichCreate(request, published, nextVersion);
        plan.setStatus(workflowService.transition(requestInfo, plan, ActionPlanAction.INITIATE, null,
                workflowEnabled));
        producer.push(config.getSaveActionPlanTopic(), request);
        return plan;
    }

    public UpdateResult update(ActionPlanRequest request) {
        ActionPlan incoming = request.getActionPlan();
        RequestInfo requestInfo = request.getRequestInfo();
        ActionPlan existing = isBlank(incoming.getId()) || isBlank(incoming.getTenantId()) ? null
                : find(ActionPlanSearchCriteria.builder().tenantId(incoming.getTenantId())
                        .ids(List.of(incoming.getId())).build(), true).stream().findFirst().orElse(null);
        if (existing == null) {
            throw new CustomException(ErrorCodes.NOT_FOUND, "Action Plan not found for id " + incoming.getId()
                    + " and tenantId " + incoming.getTenantId());
        }

        MdmsData mdms = mdmsService.getMasterData(requestInfo, incoming.getTenantId());
        boolean workflowEnabled = workflowService.isWorkflowEnabled(mdms);
        List<ActionPlan> sameProject = incoming.getProjectId() != null
                && !incoming.getProjectId().equals(existing.getProjectId())
                ? projectIdInUse(existing.getTenantId(), incoming.getProjectId()) : List.of();

        List<ValidationWarning> warnings = validator.validateUpdate(request, existing, mdms, workflowEnabled,
                sameProject);
        ActionPlanAction action = ActionPlanAction.from(incoming.getWorkflow().getAction()).orElseThrow();

        ActionPlan plan;
        if (action.modifiesData()) {
            enrichmentService.enrichUpdate(request, existing);
            plan = request.getActionPlan();
        } else {
            plan = enrichmentService.enrichStatusOnly(request, existing);
        }

        if (action != ActionPlanAction.SAVE_DRAFT) {
            if (action.isSubmission()) {
                calculationService.calculate(requestInfo, plan);
            }
            String newStatus = workflowService.transition(requestInfo, plan, action, existing.getStatus(),
                    workflowEnabled);
            plan.setStatus(newStatus);
            if (action.isSubmission()) {
                plan.setSubmittedDate(System.currentTimeMillis());
            }
            if (ActionPlanStatus.APPROVED.name().equals(newStatus)) {
                approve(requestInfo, plan);
            }
        }

        String topic = action.modifiesData() ? config.getUpdateActionPlanTopic()
                : config.getUpdateActionPlanWorkflowTopic();
        producer.push(topic, request);
        return new UpdateResult(plan, warnings);
    }

    /**
     * Publishes the version: it becomes the latest one. The persister supersedes the previous latest
     * version of the same action plan number in the same transaction.
     */
    private void approve(RequestInfo requestInfo, ActionPlan plan) {
        if (isBlank(plan.getActionPlanNumber())) {
            plan.setActionPlanNumber(idGenService.generateOne(requestInfo, plan.getTenantId(),
                    config.getActionPlanNumberIdName()));
        }
        plan.setApprovedDate(System.currentTimeMillis());
        plan.setApprovedBy(WmsUtil.userUuid(requestInfo));
        plan.setIsLatestVersion(true);
    }

    public SearchResult search(RequestInfo requestInfo, ActionPlanSearchCriteria criteria) {
        validator.validateSearch(requestInfo, criteria);
        criteria.setLimit(criteria.getLimit() == null ? config.getDefaultLimit()
                : Math.min(criteria.getLimit(), config.getMaxLimit()));
        return repository.search(criteria, true);
    }

    private List<ActionPlan> find(ActionPlanSearchCriteria criteria, boolean withChildren) {
        criteria.setLimit(config.getMaxLimit());
        return repository.search(criteria, withChildren).actionPlans();
    }

    private List<ActionPlan> projectIdInUse(String tenantId, String projectId) {
        if (isBlank(tenantId) || isBlank(projectId)) {
            return List.of();
        }
        return find(ActionPlanSearchCriteria.builder().tenantId(tenantId).projectIds(List.of(projectId)).build(),
                false).stream().filter(p -> tenantId.equals(p.getTenantId())).toList();
    }
}
