package org.upyog.draft.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.upyog.draft.repository.DraftRepository;
import org.upyog.draft.util.DraftConstants;
import org.upyog.draft.web.models.DraftDetail;
import org.upyog.draft.web.models.DraftRequest;
import org.upyog.draft.web.models.DraftResponse;
import org.upyog.draft.web.models.DraftSearchCriteria;
import org.upyog.draft.web.models.DraftSearchRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class DraftService {

    private final DraftRepository draftRepository;

    public DraftService(DraftRepository draftRepository) {
        this.draftRepository = draftRepository;
    }

    public DraftResponse save(DraftRequest request) {
        validateRequestInfo(request.getRequestInfo());
        DraftDetail draft = request.getDraft();
        validateDraftForSave(draft);

        String userUuid = resolveUserUuid(request.getRequestInfo(), draft.getCreatedBy());
        String creatorType = resolveCreatorType(request.getRequestInfo(), draft.getCreatorType());
        long now = System.currentTimeMillis();
        boolean isCreate = StringUtils.isBlank(draft.getDraftId());

        if (isCreate) {
            draft.setDraftId(UUID.randomUUID().toString());
            draft.setCreatedTime(now);
            draft.setCreatedBy(userUuid);
            draft.setCreatorType(creatorType);
            draft.setStatus(DraftConstants.STATUS_ACTIVE);
        } else {
            DraftDetail existing = draftRepository.findByDraftId(
                    draft.getDraftId(), draft.getTenantId(), userUuid);
            if (existing == null) {
                throw new CustomException("DRAFT_NOT_FOUND",
                        "Draft not found for update: " + draft.getDraftId());
            }
            draft.setCreatedBy(existing.getCreatedBy());
            draft.setCreatedTime(existing.getCreatedTime());
            if (StringUtils.isBlank(draft.getCreatorType())) {
                draft.setCreatorType(existing.getCreatorType());
            }
            if (StringUtils.isBlank(draft.getStatus())) {
                draft.setStatus(existing.getStatus());
            }
        }

        draft.setCreatedBy(userUuid);
        draft.setLastModifiedBy(userUuid);
        draft.setLastModifiedTime(now);
        if (draft.getCompletionPct() == null) {
            draft.setCompletionPct(BigDecimal.ZERO);
        }
        if (isCreate && StringUtils.isBlank(draft.getStatus())) {
            draft.setStatus(DraftConstants.STATUS_ACTIVE);
        }

        draftRepository.save(request, isCreate);
        return buildResponse(request.getRequestInfo(), draft, null, null);
    }

    public DraftResponse search(DraftSearchRequest request) {
        validateRequestInfo(request.getRequestInfo());
        DraftSearchCriteria criteria = request.getCriteria();
        if (criteria == null) {
            throw new CustomException("DRAFT_SEARCH_CRITERIA_REQUIRED", "DraftSearchCriteria is required");
        }

        String userUuid = resolveUserUuid(request.getRequestInfo(), criteria.getCreatedBy());
        criteria.setCreatedBy(userUuid);

        if (StringUtils.isBlank(criteria.getStatus())) {
            criteria.setStatus(DraftConstants.STATUS_ACTIVE);
        }
        if (criteria.getOffset() == null) {
            criteria.setOffset(0);
        }
        if (criteria.getLimit() == null) {
            criteria.setLimit(10);
        }

        List<DraftDetail> drafts = draftRepository.search(criteria);
        return DraftResponse.builder()
                .responseInfo(buildResponseInfo(request.getRequestInfo(), true))
                .drafts(drafts)
                .items(drafts)
                .build();
    }

    public DraftResponse count(DraftSearchRequest request) {
        validateRequestInfo(request.getRequestInfo());
        DraftSearchCriteria criteria = request.getCriteria();
        if (criteria == null) {
            throw new CustomException("DRAFT_SEARCH_CRITERIA_REQUIRED", "DraftSearchCriteria is required");
        }

        String userUuid = resolveUserUuid(request.getRequestInfo(), criteria.getCreatedBy());
        criteria.setCreatedBy(userUuid);

        if (StringUtils.isBlank(criteria.getStatus())) {
            criteria.setStatus(DraftConstants.STATUS_ACTIVE);
        }

        int count = draftRepository.count(criteria);
        return DraftResponse.builder()
                .responseInfo(buildResponseInfo(request.getRequestInfo(), true))
                .count(count)
                .build();
    }

    public DraftResponse delete(DraftRequest request) {
        validateRequestInfo(request.getRequestInfo());
        DraftDetail draft = request.getDraft();
        if (draft == null || StringUtils.isBlank(draft.getDraftId())) {
            throw new CustomException("DRAFT_ID_REQUIRED", "draftId is required");
        }

        String userUuid = resolveUserUuid(request.getRequestInfo(), draft.getCreatedBy());
        long now = System.currentTimeMillis();

        draft.setCreatedBy(userUuid);
        draft.setLastModifiedBy(userUuid);
        draft.setLastModifiedTime(now);
        draft.setStatus(DraftConstants.STATUS_DISCARDED);

        draftRepository.updateStatus(request);
        return buildResponse(request.getRequestInfo(), draft, null, null);
    }

    public DraftResponse markSubmitted(DraftRequest request) {
        validateRequestInfo(request.getRequestInfo());
        DraftDetail draft = request.getDraft();
        if (draft == null || StringUtils.isBlank(draft.getDraftId())) {
            throw new CustomException("DRAFT_ID_REQUIRED", "draftId is required");
        }

        String userUuid = resolveUserUuid(request.getRequestInfo(), draft.getCreatedBy());
        long now = System.currentTimeMillis();

        draft.setCreatedBy(userUuid);
        draft.setLastModifiedBy(userUuid);
        draft.setLastModifiedTime(now);
        draft.setStatus(DraftConstants.STATUS_SUBMITTED);

        draftRepository.updateStatus(request);
        return buildResponse(request.getRequestInfo(), draft, null, null);
    }

    public int purgeActiveOlderThan(long cutoffTime) {
        List<DraftDetail> stale = draftRepository.findActiveDraftsOlderThan(cutoffTime);
        for (DraftDetail draft : stale) {
            markDiscardedViaPersister(draft);
        }
        return stale.size();
    }

    public int purgeByStatusOlderThan(String status, long cutoffTime) {
        List<DraftDetail> stale = draftRepository.findSubmittedOrDiscardedOlderThan(status, cutoffTime);
        for (DraftDetail draft : stale) {
            deleteViaPersister(draft);
        }
        return stale.size();
    }

    public int reconcileOrphanedDrafts() {
        List<DraftDetail> candidates = draftRepository.findActiveDraftsWithModuleEntity();
        for (DraftDetail draft : candidates) {
            draft.setStatus(DraftConstants.STATUS_SUBMITTED);
            draft.setLastModifiedBy(draft.getCreatedBy());
            draft.setLastModifiedTime(System.currentTimeMillis());
            DraftRequest request = DraftRequest.builder()
                    .draft(draft)
                    .build();
            draftRepository.updateStatus(request);
        }
        return candidates.size();
    }

    private void markDiscardedViaPersister(DraftDetail draft) {
        draft.setStatus(DraftConstants.STATUS_DISCARDED);
        draft.setLastModifiedBy(draft.getCreatedBy());
        draft.setLastModifiedTime(System.currentTimeMillis());
        draftRepository.updateStatus(DraftRequest.builder().draft(draft).build());
    }

    private void deleteViaPersister(DraftDetail draft) {
        draftRepository.delete(DraftRequest.builder().draft(draft).build());
    }

    private void validateDraftForSave(DraftDetail draft) {
        if (draft == null) {
            throw new CustomException("DRAFT_REQUIRED", "Draft payload is required");
        }
        if (StringUtils.isBlank(draft.getTenantId())) {
            throw new CustomException("TENANT_ID_REQUIRED", "tenantId is required");
        }
        if (StringUtils.isBlank(draft.getBusinessService())) {
            throw new CustomException("BUSINESS_SERVICE_REQUIRED", "businessService is required");
        }
        if (draft.getDraftData() == null) {
            throw new CustomException("DRAFT_DATA_REQUIRED", "draftData is required");
        }
    }

    private void validateRequestInfo(RequestInfo requestInfo) {
        if (requestInfo == null || requestInfo.getUserInfo() == null
                || StringUtils.isBlank(requestInfo.getUserInfo().getUuid())) {
            throw new CustomException("USER_INFO_REQUIRED", "RequestInfo with user UUID is required");
        }
    }

    private String resolveUserUuid(RequestInfo requestInfo, String requestedUserUuid) {
        String authenticatedUuid = requestInfo.getUserInfo().getUuid();
        if (StringUtils.isNotBlank(requestedUserUuid) && !requestedUserUuid.equals(authenticatedUuid)) {
            throw new CustomException("UNAUTHORIZED_DRAFT_ACCESS", "Cannot access drafts for another user");
        }
        return authenticatedUuid;
    }

    private String resolveCreatorType(RequestInfo requestInfo, String requestedCreatorType) {
        if (StringUtils.isNotBlank(requestedCreatorType)) {
            String upper = requestedCreatorType.toUpperCase();
            if ("EMPLOYEE".equals(upper) || "USER".equals(upper)) {
                return upper;
            }
        }
        if (requestInfo != null && requestInfo.getUserInfo() != null && StringUtils.isNotBlank(requestInfo.getUserInfo().getType())) {
            String type = requestInfo.getUserInfo().getType().toUpperCase();
            if ("EMPLOYEE".equals(type)) {
                return "EMPLOYEE";
            }
        }
        return "USER";
    }

    private DraftResponse buildResponse(RequestInfo requestInfo, DraftDetail draft,
                                        List<DraftDetail> drafts, Integer count) {
        return DraftResponse.builder()
                .responseInfo(buildResponseInfo(requestInfo, true))
                .draft(draft)
                .drafts(drafts)
                .items(drafts)
                .count(count)
                .build();
    }

    private ResponseInfo buildResponseInfo(RequestInfo requestInfo, boolean success) {
        return ResponseInfo.builder()
                .apiId(requestInfo != null ? requestInfo.getApiId() : null)
                .ver(requestInfo != null ? requestInfo.getVer() : null)
                .ts(System.currentTimeMillis())
                .resMsgId(requestInfo != null ? requestInfo.getMsgId() : null)
                .msgId(requestInfo != null ? requestInfo.getMsgId() : null)
                .status(success ? "successful" : "failed")
                .build();
    }
}
