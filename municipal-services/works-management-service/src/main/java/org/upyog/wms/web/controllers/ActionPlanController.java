package org.upyog.wms.web.controllers;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.wms.repository.ActionPlanRepository.SearchResult;
import org.upyog.wms.service.ActionPlanService;
import org.upyog.wms.service.ActionPlanService.UpdateResult;
import org.upyog.wms.util.ResponseInfoFactory;
import org.upyog.wms.web.models.ActionPlan;
import org.upyog.wms.web.models.ActionPlanRequest;
import org.upyog.wms.web.models.ActionPlanResponse;
import org.upyog.wms.web.models.ActionPlanSearchCriteria;
import org.upyog.wms.web.models.Pagination;
import org.upyog.wms.web.models.RequestInfoWrapper;

@RestController
@RequestMapping("/action-plan/v1")
@RequiredArgsConstructor
public class ActionPlanController {

    private final ActionPlanService actionPlanService;
    private final ResponseInfoFactory responseInfoFactory;

    /** Registers a new plan, or a revision of an approved plan when ActionPlan.actionPlanNumber is sent. */
    @PostMapping("/_create")
    public ResponseEntity<ActionPlanResponse> create(@Valid @RequestBody ActionPlanRequest request) {
        ActionPlan plan = actionPlanService.create(request);
        ActionPlanResponse response = ActionPlanResponse.builder()
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
                .actionPlans(List.of(plan))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /** Draft save (SAVE_DRAFT), submission, verifier / approver decision or withdrawal (workflow.action). */
    @PostMapping("/_update")
    public ResponseEntity<ActionPlanResponse> update(@Valid @RequestBody ActionPlanRequest request) {
        UpdateResult result = actionPlanService.update(request);
        ActionPlanResponse response = ActionPlanResponse.builder()
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
                .actionPlans(List.of(result.actionPlan()))
                .warnings(result.warnings().isEmpty() ? null : result.warnings())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/_search")
    public ResponseEntity<ActionPlanResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                     @Valid @ModelAttribute ActionPlanSearchCriteria criteria) {
        SearchResult result = actionPlanService.search(requestInfoWrapper.getRequestInfo(), criteria);
        ActionPlanResponse response = ActionPlanResponse.builder()
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(
                        requestInfoWrapper.getRequestInfo(), true))
                .actionPlans(result.actionPlans())
                .pagination(Pagination.builder()
                        .limit(criteria.getLimit())
                        .offset(criteria.getOffset() == null ? 0 : criteria.getOffset())
                        .totalCount(result.totalCount())
                        .sortBy(criteria.getSortBy() == null ? "createdTime" : criteria.getSortBy())
                        .sortOrder(criteria.getSortOrder() == null ? "DESC" : criteria.getSortOrder().toUpperCase())
                        .build())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
