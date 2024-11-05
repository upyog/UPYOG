package org.upyog.sv.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.repository.StreetVendingRepository;
import org.upyog.sv.service.DemandService;
import org.upyog.sv.service.EnrichmentService;
import org.upyog.sv.service.StreetVendingService;
import org.upyog.sv.service.WorkflowService;
import org.upyog.sv.util.MdmsUtil;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StreetVendingServiceImpl implements StreetVendingService {

	@Autowired
	private MdmsUtil util;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private StreetVendingRepository streetVendingRepository;
	
	@Autowired
	private DemandService demandService;

	@Autowired
	private WorkflowService workflowService;

	@Override
	public StreetVendingDetail createStreetVendingApplication(StreetVendingRequest vendingRequest) {
		RequestInfo requestInfo = vendingRequest.getRequestInfo();
		String tenantId = vendingRequest.getStreetVendingDetail().getTenantId().split("\\.")[0];
		if (vendingRequest.getStreetVendingDetail().getTenantId().split("\\.").length == 1) {
			throw new CustomException(StreetVendingConstants.INVALID_TENANT,
					"Application cannot be created at StateLevel");
		}
		
		Object mdmsData = util.mDMSCall(requestInfo, tenantId);
		log.info("MDMS master data : " + mdmsData);
		
		enrichmentService.enrichCreateStreetVendingRequest(vendingRequest);
	
		workflowService.updateWorkflowStatus(vendingRequest);
		
		//TODO: Move demand generation to update booking working api
	//	demandService.createDemand(vendingRequest, tenantId);
		
		streetVendingRepository.save(vendingRequest);
		
		return vendingRequest.getStreetVendingDetail();
	}

	@Override
	public List<StreetVendingDetail> getStreetVendingDetails(RequestInfo info,
			StreetVendingSearchCriteria streetVendingSearchCriteria) {
		List<StreetVendingDetail> applications = streetVendingRepository
				.getStreetVendingApplications(streetVendingSearchCriteria);

		if (CollectionUtils.isEmpty(applications))
			return new ArrayList<>();
		return applications;
	}

}
