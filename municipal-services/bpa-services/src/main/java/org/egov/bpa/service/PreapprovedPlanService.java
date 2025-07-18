package org.egov.bpa.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.egov.bpa.config.BPAConfiguration;
import org.egov.bpa.repository.PreapprovedPlanRepository;
import org.egov.bpa.util.BPAErrorConstants;
import org.egov.bpa.util.BPAUtil;
import org.egov.bpa.web.model.PreapprovedPlan;
import org.egov.bpa.web.model.PreapprovedPlanRequest;
import org.egov.bpa.web.model.PreapprovedPlanSearchCriteria;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PreapprovedPlanService {

	@Autowired
	private PreapprovedPlanRepository repository;

	@Autowired
	private BPAUtil util;

	@Autowired
	private BPAConfiguration config;
	
	@Autowired
	EnrichmentService enrichmentService;

	/**
	 * does all the validations required to create BPA Record in the system
	 * 
	 * @param bpaRequest
	 * @return
	 */
	public PreapprovedPlan create(PreapprovedPlanRequest preapprovedPlanRequest) {
		RequestInfo requestInfo = preapprovedPlanRequest.getRequestInfo();
		//String tenantId = preapprovedPlanRequest.getPreapprovedPlan().getTenantId().split("\\.")[0];
		enrichmentService.enrichPreapprovedPlanCreateRequestV2(preapprovedPlanRequest);
		//Object mdmsData = util.mDMSCall(requestInfo, tenantId);
		// TODO validations
		repository.save(preapprovedPlanRequest);
		return preapprovedPlanRequest.getPreapprovedPlan();
	}

	/**
	 * Returns the bpa with enriched owners from user service
	 * 
	 * @param criteria    The object containing the parameters on which to search
	 * @param requestInfo The search request's requestInfo
	 * @return List of bpa for the given criteria
	 */
	public List<PreapprovedPlan> getPreapprovedPlanFromCriteria(PreapprovedPlanSearchCriteria criteria) {
		List<PreapprovedPlan> preapprovedPlans = repository.getPreapprovedPlansData(criteria);
		if (preapprovedPlans.isEmpty())
			return Collections.emptyList();
		return preapprovedPlans;
	}
	
	/**
	 * Updates the preapproved plan
	 * 
	 * @param preapprovedPlanRequest The update Request
	 * @return Updated PreapprovedPlan
	 */
	@SuppressWarnings("unchecked")
	public PreapprovedPlan update(PreapprovedPlanRequest preapprovedPlanRequest) {
		RequestInfo requestInfo = preapprovedPlanRequest.getRequestInfo();
		String tenantId = preapprovedPlanRequest.getPreapprovedPlan().getTenantId().split("\\.")[0];
		// Object mdmsData = util.mDMSCall(requestInfo, tenantId);
		// TODO : validations if any
		PreapprovedPlan preapprovedPlan = preapprovedPlanRequest.getPreapprovedPlan();

		if (preapprovedPlan.getId() == null) {
			throw new CustomException(BPAErrorConstants.UPDATE_ERROR,
					"Preapproved plan not found in the System" + preapprovedPlan);
		}

		List<PreapprovedPlan> searchResult = getPreapprovedPlansWithId(preapprovedPlanRequest);
		if (CollectionUtils.isEmpty(searchResult) || searchResult.size() > 1) {
			throw new CustomException(BPAErrorConstants.UPDATE_ERROR,
					"Failed to Update the Application, Found None or multiple Preapproved plans!");
		}
		preapprovedPlan.setAuditDetails(searchResult.get(0).getAuditDetails());
		enrichmentService.enrichPreapprovedPlanUpdateRequest(preapprovedPlanRequest);
		repository.update(preapprovedPlanRequest);
		return preapprovedPlanRequest.getPreapprovedPlan();
	}
	
	/**
	 * Returns preapprovedPlans from db for the update request
	 * 
	 * @param request The update request
	 * @return List of preapprovedPlans
	 */
	public List<PreapprovedPlan> getPreapprovedPlansWithId(PreapprovedPlanRequest request) {
		PreapprovedPlanSearchCriteria criteria = new PreapprovedPlanSearchCriteria();
		List<String> ids = new LinkedList<>();
		ids.add(request.getPreapprovedPlan().getId());
		criteria.setTenantId(request.getPreapprovedPlan().getTenantId());
		criteria.setIds(ids);
		List<PreapprovedPlan> preapprovedPlans = repository.getPreapprovedPlansData(criteria);
		return preapprovedPlans;
	}

}
